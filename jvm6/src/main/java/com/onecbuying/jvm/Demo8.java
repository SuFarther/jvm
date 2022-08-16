package com.onecbuying.jvm;

import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Opcodes;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Demo8
 * @company 公司
 * @Description 演示永久代内存溢出  java.lang.OutOfMemoryError: PermGen space
 * -XX:MaxPermSize=8m
 * @createTime 2022年08月16日 22:07:07
 */
public class Demo8 extends ClassLoader{
    public static void main(String[] args) {
        int j = 0;
        try {
            Demo8 test = new Demo8();
            for (int i = 0; i < 20000; i++, j++) {
                ClassWriter cw = new ClassWriter(0);
                cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, "Class" + i, null, "java/lang/Object", null);
                byte[] code = cw.toByteArray();
                test.defineClass("Class" + i, code, 0, code.length);
            }
        } finally {
            System.out.println(j);
        }
    }
}
