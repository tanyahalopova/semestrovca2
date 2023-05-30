class BTreeNode {
    long operation = 0;
    int[] keys;
    int MinDeg;
    BTreeNode[] children;
    int num;
    boolean isLeaf;

    public BTreeNode(int deg, boolean isLeaf) {
        this.MinDeg = deg;
        this.isLeaf = isLeaf;
        this.keys = new int[2 * this.MinDeg - 1];
        this.children = new BTreeNode[2 * this.MinDeg];
        this.num = 0;
    }


    public int findKey(int key) {
        int idx = 0;
        while (idx < num && keys[idx] < key)
            ++idx;
        return idx;
    }


    public void remove(int key) {
        operation++;
        int idx = findKey(key);
        if (idx < num && keys[idx] == key) {
            if (isLeaf)
                removeFromLeaf(idx);
            else
                removeFromNonLeaf(idx);
        } else {
            if (isLeaf) {
                return;
            }
            boolean flag = idx == num;
            if (children[idx].num < MinDeg)
                fill(idx);
            if (flag && idx > num)
                children[idx - 1].remove(key);
            else
                children[idx].remove(key);
        }
    }

    public void removeFromLeaf(int idx) {
        for (int i = idx + 1; i < num; ++i) {
            operation++;
            keys[i - 1] = keys[i];
        }
        operation++;
        num--;
    }

    public void removeFromNonLeaf(int idx) {
        operation++;
        int key = keys[idx];
        if (children[idx].num >= MinDeg) {
            operation++;
            int pred = getPred(idx);
            keys[idx] = pred;
            children[idx].remove(pred);
        }
        else if (children[idx + 1].num >= MinDeg) {
            operation++;
            int succ = getSucc(idx);
            keys[idx] = succ;
            children[idx + 1].remove(succ);
        } else {
            merge(idx);
            children[idx].remove(key);
        }
    }

    public int getPred(int idx) {

        BTreeNode cur = children[idx];
        while (!cur.isLeaf) {
            operation++;
            cur = cur.children[cur.num];
        }
        operation++;
        return cur.keys[cur.num - 1];
    }

    public int getSucc(int idx) {


        BTreeNode cur = children[idx + 1];
        while (!cur.isLeaf) {
            operation++;
            cur = cur.children[0];
        }
        operation+=2;
        return cur.keys[0];
    }


    public void fill(int idx) {
        if (idx != 0 && children[idx - 1].num >= MinDeg)
            borrowFromPrev(idx);
        else if (idx != num && children[idx + 1].num >= MinDeg)
            borrowFromNext(idx);
        else {
            if (idx != num)
                merge(idx);
            else
                merge(idx - 1);
        }
    }

    public void borrowFromPrev(int idx) {

        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx - 1];


        for (int i = child.num - 1; i >= 0; --i)
            child.keys[i + 1] = child.keys[i];

        if (!child.isLeaf) {
            for (int i = child.num; i >= 0; --i)
                child.children[i + 1] = child.children[i];
        }
        child.keys[0] = keys[idx - 1];
        if (!child.isLeaf)
            child.children[0] = sibling.children[sibling.num];

        keys[idx - 1] = sibling.keys[sibling.num - 1];
        child.num += 1;
        sibling.num -= 1;
    }

    public void borrowFromNext(int idx) {

        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx + 1];

        child.keys[child.num] = keys[idx];

        if (!child.isLeaf)
            child.children[child.num + 1] = sibling.children[0];

        keys[idx] = sibling.keys[0];

        for (int i = 1; i < sibling.num; ++i)
            sibling.keys[i - 1] = sibling.keys[i];

        if (!sibling.isLeaf) {
            for (int i = 1; i <= sibling.num; ++i)
                sibling.children[i - 1] = sibling.children[i];
        }
        child.num += 1;
        sibling.num -= 1;
    }

    public void merge(int idx) {

        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx + 1];
        child.keys[MinDeg - 1] = keys[idx];
        for (int i = 0; i < sibling.num; ++i) {
            operation++;
            child.keys[i + MinDeg] = sibling.keys[i];
        }
        if (!child.isLeaf) {
            for (int i = 0; i <= sibling.num; ++i) {
                operation++;
                child.children[i + MinDeg] = sibling.children[i];
            }
        }

        for (int i = idx + 1; i < num; ++i) {
            operation++;
            keys[i - 1] = keys[i];
        }
        for (int i = idx + 2; i <= num; ++i) {
            operation++;
            children[i - 1] = children[i];
        }

        child.num += sibling.num + 1;
        num--;
    }


    public void insertNotFull(int key) {
        operation++;
        int i = num - 1;

        if (isLeaf) {
            while (i >= 0 && keys[i] > key) {
                operation+=2;
                keys[i + 1] = keys[i];
                i--;
            }
            keys[i + 1] = key;
            num = num + 1;
            operation+=2;
        } else {
            while (i >= 0 && keys[i] > key) {
                operation++;
                i--;
            }
            if (children[i + 1].num == 2 * MinDeg - 1) {
                splitChild(i + 1, children[i + 1]);
                if (keys[i + 1] < key)
                    i++;
                operation+=3;
            }
            children[i + 1].insertNotFull(key);
            operation++;
        }
    }


    public void splitChild(int i, BTreeNode y) {
        BTreeNode z = new BTreeNode(y.MinDeg, y.isLeaf);
        z.num = MinDeg - 1;
        operation += 2;
        // Pass the properties of y to z
        for (int j = 0; j < MinDeg - 1; j++) {
            operation++;
            z.keys[j] = y.keys[j + MinDeg];
        }
        if (!y.isLeaf) {
            for (int j = 0; j < MinDeg; j++) {
                operation++;
                z.children[j] = y.children[j + MinDeg];
            }
        }
        y.num = MinDeg - 1;
        operation++;
        for (int j = num; j >= i + 1; j--) {
            operation++;
            children[j + 1] = children[j];
        }
        children[i + 1] = z;
        operation++;
        for (int j = num - 1; j >= i; j--) {
            operation++;
            keys[j + 1] = keys[j];
        }
        keys[i] = y.keys[MinDeg - 1];

        num = num + 1;
        operation += 2;
    }


    public void traverse() {
        int i;
        for (i = 0; i < num; i++) {
            if (!isLeaf)
                children[i].traverse();
            System.out.printf(" %d", keys[i]);
        }

        if (!isLeaf) {
            children[i].traverse();
        }
    }


    public BTreeNode search(int key) {
        operation++;
        int i = 0;
        while (i < num && key > keys[i]) {
            operation++;
            i++;
        }
        operation++;
        if (i != 9 && keys[i] == key)
            return this;
        operation++;
        if (isLeaf)
            return null;
        operation++;
        return children[i].search(key);
    }
}


class BTree {
    long opertaion = 0;
    BTreeNode root;
    int MinDeg;

    // Constructor
    public BTree(int deg) {
        this.root = null;
        this.MinDeg = deg;
    }

    public void traverse() {
        if (root != null) {
            root.traverse();
        }
    }

    public BTreeNode search(int key) {
        root.search(key);
        opertaion += root.operation;
        root.operation = 0;
        return root == null ? null : root.search(key);
    }

    public void insert(int key) {

        if (root == null) {

            root = new BTreeNode(MinDeg, true);
            root.keys[0] = key;
            root.num = 1;
            opertaion += 3;
        } else {
            if (root.num == 2 * MinDeg - 1) {
                BTreeNode s = new BTreeNode(MinDeg, false);
                s.children[0] = root;
                s.splitChild(0, root);
                opertaion += s.operation;
                s.operation = 0;
                int i = 0;
                if (s.keys[0] < key)
                    i++;
                s.children[i].insertNotFull(key);
                opertaion += s.operation;
                s.operation = 0;
                root = s;
                opertaion += 8;
            } else
                opertaion++;
            root.insertNotFull(key);
            opertaion += root.operation;
            root.operation = 0;
        }
    }

    public void remove(int key) {
        if (root == null) {
            System.out.println("The tree is empty");
            opertaion+=2;
            return;
        }
        root.remove(key);
        opertaion+=3;
        if (root.num == 0) {
            if (root.isLeaf)
                root = null;
            else
                root = root.children[0];
        }
    }

    public long getOperationsCount() {
        return opertaion;
    }
}
