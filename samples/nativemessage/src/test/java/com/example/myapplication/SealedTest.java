package com.example.myapplication;

import org.junit.Test;

public class SealedTest {
    @Test
    public void testSealed(){
        Base b = SealedTestKt.getSealed2();
        b.getName();
//        Sealed2 s = (Sealed2)b;
//        System.out.println(s.getText());

        if (b instanceof Sealed1) {
            System.out.println("SEALED1");
        }else  if (b instanceof Sealed2) {
            System.out.println("SEALED2");
        }
    }
}
