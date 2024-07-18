package hellojpa;

public class ValueMain {
    public static void main(String[] args) {
        /**
         * 기본 값 타입 설명
         */
        int a = 10;
        int b = a; // copy value
        a= 20;

//        System.out.println("a = " + a);
//        System.out.println("b = " + b);

        Integer refA = Integer.valueOf(10);
        Integer refB = refA;

        // 자바에서는 Wrapper Class의 값을 변경할 수 없기 때문에 side effect가 발생하지 않음!

        System.out.println("refA = " + refA);
        System.out.println("refB = " + refB);
        System.out.println("refA == refB: " + (refA == refB));

    }
}
