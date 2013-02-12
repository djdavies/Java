class LinkedList {

    class Node {
        int data;
        Node next;
        public Node (int d, Node n) {
            data = d;
            next = n;
        }
    }

    Node head;

    public LinkedList() {
        head = null;
    }

    public boolean isEmpty() {
        return (head == null);
    }

    public void insert(int d) {
        head = new Node (d, head);
    }

    public int delete() {
        if (head == null) {
            System.err.println ("LinkedList: tried to delete from empty list");
            return -1;
        }
        int res = head.data;
        head = head.next;
        return res;
    }

    public boolean has (int d) {
        Node h = head;
        while (h != null) {
            if (h.data == d) {
                return true;
            }
            h = h.next;
        }
        return false;
    }

    public void print () {
        Node h = head;
        System.out.print ("(");
        while (h != null) {
            System.out.print(h.data);
            h = h.next;
            if (h != null) {
                System.out.print (",");
            }
        }
        System.out.println (")");
    }

}
