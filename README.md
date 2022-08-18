# 深入JAVA 的JVM核心原理解决线上各种故障
## 目录
### 什么是JVM?
Java Virtual Machine - java程序的运行环境（java二进制字节码的运行环境）

### JVM好处？
1. 一次编写，到处运行的基石【重点】
2. 自动内存管理，垃圾回收功能【重点】
3. 数据下标越界检查
4. 多态，面向对象编程

### JVM、JRE、JDK三者比较：
![JVM、JRE、JDK三者比较](https://img-blog.csdnimg.cn/20200730200026124.png)

### 学习JVM有什么用？
```
1、面试
2、理解底层的实现原理
3、中高级程序员的必备技能
```

### JVM组成有哪些？
![JVM组成有哪些？1](https://img-blog.csdnimg.cn/20200801150851388.png)
![JVM组成有哪些？2](https://img-blog.csdnimg.cn/20200730200218485.png)

### 常见的JVM
![常见的JVM](https://img-blog.csdnimg.cn/20200730200140764.png)
### JAVA 内存结构组成
1. 程序计时器
2. 虚拟机栈
3. 本地方法栈
4. 堆
5. 方法区

<hr/>

### 1、程序计数器
![常见的JVM](https://img-blog.csdnimg.cn/2020073020344595.png)

#### 1.1 程序计数器定义
 > Program Counter Register程序计数器（寄存器）

#### 1.2 程序计数器作用
<font size="3" >**作用:** 是记住下一条jvm指令的执行地址</font>

<font size="3" >**特点:** <font color="#f33b45">线程私有的； 不存在内存溢出，也是JVM规范中唯一没有OutOfMemoryError的区域</font></font>

<font size="3" >**二进制字节码:** JVM指令 —>  解释器  —> 机器码 —> CPU</font>

<font size="3" >**程序计数器**  记住下一条jvm指令的执行地址，硬件方面通过【寄存器】实现</font>

示例： 二进制字节码：jvm指令       java 源代码
![二进制字节码](https://img-blog.csdnimg.cn/20200730203706398.png)

<hr/>

### 2、虚拟机栈（-Xss256k）
![虚拟机栈](https://img-blog.csdnimg.cn/20200730203900544.png)

先了解一程数据结构
- 栈Stack，先进后出FILO
- 栈-线程运行需要的内存空间
- 栈帧-每个方法运行时需要的内存

#### 2.1 栈定义
Java Virtual Machine Stacks (Java虚拟机栈)
1. 每个线程运行时所需要的内存，称为虚拟机栈
2. 每个栈由多个栈帧（Frame）组成，对应着每次方法调用时所占用的内存
3. 每个线程只能有一个活动栈帧，对应着当前正在执行的那个方法

#### 2.2 栈问题
1. 垃圾回收是否涉及栈内存？   答案：栈内存不涉及垃圾回收
2. 栈内存分配越大越好吗？ 答案：栈内存不是越大越好，如果设置过大，会影响可用线程的数量；比如-Xss1m、-Xss2m，在总内存不变的情况下，可用线程数量会减少
3. 方法内的局部变量是否线程安全？   答案：方法内的局部变量是线程安全，因为方法内的局部变量各自在自已独立的内存中；如果是static int 就是线程共享的，就不是线程安全；主要看变量是否是线程共享、还是线程私有

核心1：<font color="#f33b45" size="3">如果方法内局部变量没有逃离方法的作用范围，它是线程安全的</font><br/>
核心2：<font color="#f33b45" size="3">如果是局部变量引用了对象，并逃离方法的作用范围，需要考虑线程安全</font><br/>

#### 2.3 栈内存溢出（-Xss256k）
栈帧过多导致栈内存溢出，比如：递归，我们生产环境推荐尽量不使用递归<br/>
栈帧过大导致栈内存溢出

#### 线程运行诊断（附案例）
##### 2.3.1 cpu占用过高，如何诊断案例 
<font color="#f33b45" size="3">1. 用top定位哪个进程对cpu的占用过高</font>
![用top定位哪个进程对cpu的占用过高](https://img-blog.csdnimg.cn/20200730204732769.png)
<font color="#f33b45" size="3">2. ps H -eo pid,tid,%cpu     查看linux所有进程、线程、CPU消耗情况</font>
![ 查看linux所有进程、线程、CPU消耗情况](https://img-blog.csdnimg.cn/20200730204847966.png)<br/>
<font color="#f33b45" size="3">3. ps H -eo pid,tid,%cpu | grep 进程id    用ps命令进一步定位哪个线程引起的CPU占用过高</font><br/>
![用ps命令进一步定位哪个线程引起的CPU占用过高](https://img-blog.csdnimg.cn/20200730204926135.png)<br/>
<font color="#f33b45" size="3">4. jstack 进程pid        需要将十进制的线程id转成16进制; 可以根据线程id找到有问题的线程，进一步定位问题代码的源码行号</font>
![需要将十进制的线程id转成16进制; 可以根据线程id找到有问题的线程，进一步定位问题代码的源码行号](https://img-blog.csdnimg.cn/20200730205019222.png)
<font color="#f33b45" size="3">通过上述方式找到了源代码CPU消耗过高的文件及行号</font>
![CPU消耗过高的文件及行号](https://img-blog.csdnimg.cn/2020073020511734.png)

##### 2.3.2 程序运行很长时间没有结果，如何诊断案例
``` nohup java -cp /root/JvmLearn-1.0-SNAPSHOT.jar com.jvm.stack.T07_StackDeadLock & ``` 

![程序运行很长时间没有结果，如何诊断案例](https://img-blog.csdnimg.cn/20200730205246527.png)
![程序运行很长时间没有结果，如何诊断案例](https://img-blog.csdnimg.cn/20200730205339226.png)

<hr/>

### 3、本地方法栈（不是Java编写的代码，通过C/C++）
![本地方法栈](https://img-blog.csdnimg.cn/20200731193457234.png)

### 4、堆（-Xmx8m）
#### 4.1 堆的定义
- 通过new 关键字，创建对象都会使用堆内存
- 特点：
  - 它是线程共享的，堆中对象需要考虑线程安全的问题
  - 有垃圾回收机制
    ![堆的定义](https://img-blog.csdnimg.cn/20200731193650549.png)
#### 4.2 堆内存溢出问题及生产建议
<font  color="#f33b45" size="3"> 代码参考：com.jvm.t02_heap.T01_HeapOutOfMemoryError</font>

```
/**
 * 演示堆内存溢出 java.lang.OutOfMemoryError: Java heap space
 * -Xmx8m
 */
 
public class T01_HeapOutOfMemoryError {
 
    public static void main(String[] args) {
        int i = 0;
        try {
            List<String> list = new ArrayList<>();
            String a = "hello";
            while (true) {
                list.add(a); // hello, hellohello, hellohellohellohello ...
                a = a + a;  // hellohellohellohello
                i++;
                TimeUnit.MILLISECONDS.sleep(2000);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println(i);
        }
    }
 
}
```
<font color="#f33b45" size="3">生产环境建议：</font><font size="3">如果内存比较大，内存溢出不会那么快的暴露；这时，我们可以将堆内存调小，让内存溢出尽早暴露</font>
#### 4.3 堆内存诊断工具介绍，及实操(JDK9版本尝试)
 - jps工具：查看当前系统中有哪些java进程
 - jmap工具：查看堆内存占用情况 jmap -heap pid
 - jstack 工具：线程监控
 - jconsole工具：图形界面的，多功能的检测工具，可以连续监测
 - jvisualvm工具：图形界面的，多功能的检测工具，可以连续监测；还有dump
  
  <font color="#f33b45" size="3">代码参考：com.jvm.t02_heap.T02_HeapUseUpAndDown</font>

```
/**
 * 演示堆内存
 */
public class T02_HeapUseUpAndDown {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("1...");
        Thread.sleep(30000);
        byte[] array = new byte[1024 * 1024 * 10]; // 10 Mb
        System.out.println("2...");
        Thread.sleep(20000);
        array = null;
        System.gc();
        System.out.println("3...");
        Thread.sleep(1000000L);
    }
}
```
1. jps
2. Jmap -head pid    查看堆内存占用
3. 在控制台上使用jconsole

##### 4.3.1 垃圾回收后，内存占用仍然很高，排查方式案例
<font color="#f33b45" size="3">代码参考：com.jvm.t02_heap.T03_HeapAfterGcMemStillHigh</font>
```
/**
 * 演示查看对象个数 堆转储 dump
 */
public class T03_HeapAfterGcMemStillHigh {
    public static void main(String[] args) throws InterruptedException {
        List<Student> students = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            students.add(new Student());
//            Student student = new Student();
        }
        Thread.sleep(1000000000L);
    }
}
 
class Student {
    private byte[] big = new byte[1024 * 1024];
}
```
<font size="3">解决方式：jvisualvm   可以使用dump，查找最大的对象堆转储 dump（基于上述问题，使用工具进行查看）; 在测试环境下，我们可以开启dump文件记录，然后将dump文件导入到jvisualvm工具查看，占用最多的内存的对象是哪些</font>
![ 垃圾回收后，内存占用仍然很高](https://img-blog.csdnimg.cn/2020073119413854.png)

### 5、元空间/方法区（-XX:MaxMetaspaceSize=8m）
![元空间/方法区](https://img-blog.csdnimg.cn/20200731194501759.png)
#### 5.1 JVM方法区定义
- 线程共享
- 在JVM启动时创建，在逻辑上属于堆的一部分（看厂商实现）
- 方法区也可能会内存溢出
#### 5.2 方法区组成
![方法区组成](https://img-blog.csdnimg.cn/20200731194727503.png)
#### 5.3 方法区内存溢出
1.8 以前会导致永久代内存溢出
 - 演示永久代内存溢出  java.lang.OutOfMemoryError: PerGen space
 - -XX:MaxPerSize=8m
1.8 之后会导致元空间内存溢出（系统内存）
 - 演示元空间内存溢出  java.lang.OutOfMemoryError: Metaspace
 - -XX:MaxMetaspaceSize=8m

##### 5.3.1 元空间内存溢出演示案例
<font color="#f33b45" size="3">Jdk1.8 参考代码：com.jvm.t03_metaspace.T01_MetaspaceOutOfMemoryError</font>
 - 演示元空间内存溢出 java.lang.OutOfMemoryError: Metaspace
 - -XX:MaxMetaspaceSize=8m
   ![元空间内存溢出](https://img-blog.csdnimg.cn/20200802135551748.png)
##### 5.3.2 生产环境出现元空间内存溢出问题，应该锁定这些方面
&nbsp;&nbsp;&nbsp;<font size="3">虽然我们自己编写的程序没有大量使用动态加载类，但如果我们在使用外部一些框架时，可能大量动态加载类，就可能会导致元空间内存溢出。</font>

<font size="3">场景（动态加载类），如果</font><font color="#f33b45" size="3">框架使用不合理也会导致方法区内存溢出</font>
 - spring
 - mybatis
#### 5.4 运行时常量池
 - 常量池，就是一张表，虚拟机指令根据这张常量表找到要执行的类名、方法名、参数类型、字面量等信息
 - 运行时常量池，常量池是*.class 文件中的，当该类被加载，它的常量池信息就会放入运行时常量池，并把里面的符号地址变为真实地址
   ![运行时常量池](https://img-blog.csdnimg.cn/20200731194727503.png)
##### 5.4.1 字符串常量池JVM字节码方面原理演示
```
// 二进制字节码（类基本信息，常量池，类方法定义，包含了虚拟机指令）
public class T02_StringHelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}
```
<font color="#f33b45" size="3">将上述编译好的class文件进行反汇编：Javap -v HelloWord.class   反编译结果如下：</font>
```
D:\software\Java\jdk1.8.0_211\bin\javap.exe -v com.jvm.t03_metaspace.T02_MetaspaceConstantPool
Classfile /D:/lei_test_project/idea_workspace/Jvm_Learn/target/classes/com/jvm/t03_metaspace/T02_MetaspaceConstantPool.class
  Last modified 2020-7-29; size 623 bytes
  MD5 checksum 6b5272fbb2c0ca06c0e460818756710d
  Compiled from "T02_MetaspaceConstantPool.java"
public class com.jvm.t03_metaspace.T02_MetaspaceConstantPool
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Methodref          #6.#20         // java/lang/Object."<init>":()V
   #2 = Fieldref           #21.#22        // java/lang/System.out:Ljava/io/PrintStream;
   #3 = String             #23            // hello world!
   #4 = Methodref          #24.#25        // java/io/PrintStream.println:(Ljava/lang/String;)V
   #5 = Class              #26            // com/jvm/t03_metaspace/T02_MetaspaceConstantPool
   #6 = Class              #27            // java/lang/Object
   #7 = Utf8               <init>
   #8 = Utf8               ()V
   #9 = Utf8               Code
  #10 = Utf8               LineNumberTable
  #11 = Utf8               LocalVariableTable
  #12 = Utf8               this
  #13 = Utf8               Lcom/jvm/t03_metaspace/T02_MetaspaceConstantPool;
  #14 = Utf8               main
  #15 = Utf8               ([Ljava/lang/String;)V
  #16 = Utf8               args
  #17 = Utf8               [Ljava/lang/String;
  #18 = Utf8               SourceFile
  #19 = Utf8               T02_MetaspaceConstantPool.java
  #20 = NameAndType        #7:#8          // "<init>":()V
  #21 = Class              #28            // java/lang/System
  #22 = NameAndType        #29:#30        // out:Ljava/io/PrintStream;
  #23 = Utf8               hello world!
  #24 = Class              #31            // java/io/PrintStream
  #25 = NameAndType        #32:#33        // println:(Ljava/lang/String;)V
  #26 = Utf8               com/jvm/t03_metaspace/T02_MetaspaceConstantPool
  #27 = Utf8               java/lang/Object
  #28 = Utf8               java/lang/System
  #29 = Utf8               out
  #30 = Utf8               Ljava/io/PrintStream;
  #31 = Utf8               java/io/PrintStream
  #32 = Utf8               println
  #33 = Utf8               (Ljava/lang/String;)V
{
  public com.jvm.t03_metaspace.T02_MetaspaceConstantPool();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 11: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Lcom/jvm/t03_metaspace/T02_MetaspaceConstantPool;
 
  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=1, args_size=1
         0: getstatic     #2                  // Field java/lang/System.out:Ljava/io/PrintStream;
         3: ldc           #3                  // String hello world!
         5: invokevirtual #4                  // Method java/io/PrintStream.println:(Ljava/lang/String;)V
         8: return
      LineNumberTable:
        line 13: 0
        line 14: 8
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       9     0  args   [Ljava/lang/String;
}
SourceFile: "T02_MetaspaceConstantPool.java"
 
Process finished with exit code 0
```
#### 5.5 StringTable
##### 5.5.1 StringTable常量池与串池的关系
<font color="#f33b45" size="3">代码参考：com.jvm.t03_metaspace.T03_MetaspaceStringTable</font>
```
// StringTable [ "a", "b" ,"ab" ]  hashtable 结构，不能扩容
 
public class T03_StringTable {
    // 常量池中的信息，都会被加载到运行时常量池中， 这时 a b ab 都是常量池中的符号，还没有变为 java 字符串对象
    // ldc #2 会把 a 符号变为 "a" 字符串对象
    // ldc #3 会把 b 符号变为 "b" 字符串对象
    // ldc #4 会把 ab 符号变为 "ab" 字符串对象
 
    public static void main(String[] args) {
        String s1 = "a"; // 懒惰的
        String s2 = "b";
        String s3 = "ab";
        String s4 = s1 + s2; // new StringBuilder().append("a").append("b").toString()  new String("ab")
        String s5 = "a" + "b";  // javac 在编译期间的优化，结果已经在编译期确定为ab
 
        System.out.println(s3 == s4);
        System.out.println(s3 == s5);
    }
}
```
<font size="3" >javap -v T03_MetaspaceStringTable.class 反编译如下：</font>

![StringTable常量池与串池的关系](https://img-blog.csdnimg.cn/20200731200224394.png)
##### 5.5.2 StringTable 字符串延迟加载
![StringTable常量池与串池的关系](https://img-blog.csdnimg.cn/20200731200358726.png)
#### 5.6 StringTable特性
 - 常量池中的字符串仅是符号，第一次用到时才变为对象
 - 利用串池的机制，来避免重复创建字符串对象
 - 字符串变量拼接的原理是 StringBuilder (JDK1.8)
 - 字符串常量拼接的原理是编译期优化
 - 可以使用intern 方法，主动将串池中还没有的字符串对象放入串池
   - 1.8 将这个字符串对象尝试放放串池，如果有则并不会放入，如果没有则放入串池，会把串池中的对象返回
   - 1.6 将这个字符串对象尝试放入串池，如果有则并不会放入，如果没有会把对象复制一份，放入串池，会把串池中的对象返回

StringTable_intern_1.8

<font color="#f33b45" size="3">代码参考：com.jvm.t03_metaspace.T05_TestString02</font>

![StringTable特性](https://img-blog.csdnimg.cn/20200731200551697.png)

#### 5.7 StringTable位置
 - JDK1.6版本，字符串常量池是在永久代中；
 - DK1.7 及之后版本的 JVM 已经将运行时常量池从方法区中移了出来，在 Java 堆（Heap）中开辟了一块区域存放运行时常量池。
 - JDK1.8开始，取消了Java方法区，取而代之的是位于直接内存的元空间（metaSpace）。

JDK1.6 与 JDK1.8字符串常量池对比
![字符串常量池对比](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL21tYml6X3BuZy83SVFQQ2J1bVRqbmpGVE1pY0xnUnl2QlJrRWY1WEJadFQyeTIyUTdPUnVtSUg0ckRFV3ppY1A5ZlpaSjJlaWNkcERxclVLNEdwbFZlTFl5TlVscmJ0M2pTQS82NDA?x-oss-process=image/format,png)

##### 5.7.1 JDK1.8 字符串常量池在堆中实例验证
<font color="#f33b45" size="3">代码参考： com.jvm.t03_metaspace.T07_StringTablePosition</font>
![JDK1.8 字符串常量池在堆中实例验证](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xcGljLmNuL21tYml6X3BuZy83SVFQQ2J1bVRqbmpGVE1pY0xnUnl2QlJrRWY1WEJadFRWNHJLNUNMSlhzelBkN0RpY0EzeERubGx5RHM2U1pTQWlhR2hpY09laGJ0TmY3VmRrQlBLYVRqTncvNjQw?x-oss-process=image/format,png)


#### 5.8 StringTable垃圾回收
因为在jdk1.8中，字符串常量池是放在堆中，如果堆空间不足，字符串常量池也会进行垃圾回收
<font color="#f33b45" size="3">代码参考：com.jvm.t04_stringtable.T08_StringTableGc</font>
```
/**
 * 演示 StringTable 垃圾回收
 * -Xmx10m -XX:+PrintStringTableStatistics -XX:+PrintGCDetails -verbose:gc
 */
 
// 因为在jdk1.8中，字符串常量池是放在堆中，如果堆空间不足，字符串常量池也会进行垃圾回收
 
public class T08_StringTableGc {
    public static void main(String[] args) throws InterruptedException {
        int i = 0;
        try {
            for (int j = 0; j < 10000; j++) { // 前后运行100次、10000次，进行对比。j=100, j=10000
                String.valueOf(j).intern();
                i++;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            System.out.println(i);
        }
    }
}
```
#### 5.9 StringTable 性能调优（案例）
<font color="#f33b45" size="3">代码参考：com.jvm.t04_stringtable.T09_StringTableSizeForPerformance</font>
 - 调整 -XX:StringTableSize=桶个数
 - 考虑将字符串对象是否入池

##### <font color="#f33b45">5.9.1 使用-XX:StringTableSize=大小参数增加桶的数量使StringTable性能增加案例</font>
| 序号  | StringTableSize大小	 |                                                                                                                                                                                                                                                                              运行耗时（单位毫秒） |
|:----|:------------------:|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| 1   |        1009        | 11444 <img alt="" height="22" src="https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xbG9nby5jbi9tbWJpel9wbmcvN0lRUENidW1Uam5qRlRNaWNMZ1J5dkJSa0VmNVhCWnRUODNnTXB5Zzk3b1lMZ29Td1NRS21yM2xYQTFvZWdUbmc3Z0h6Q1AxTFlsaWF0dnRyUVBBUVpMUS8w?x-oss-process=image/format,png" width="22"> |
| 2   |       10009        | 1765 <img alt="" height="21" src="https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xbG9nby5jbi9tbWJpel9wbmcvN0lRUENidW1Uam5qRlRNaWNMZ1J5dkJSa0VmNVhCWnRUbXRVaWF0cTdYbzFZQ2ZpYzhoNGlhaWFZRmFmaGx4dWRxTXRnNmlhSXlRczJGcHg3Y0xpYXpvTGo1cUlRLzA?x-oss-process=image/format,png" width="21"> |
| 3   |      100009	     |       430 <img alt="" height="20" src="https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xbG9nby5jbi9tbWJpel9wbmcvN0lRUENidW1Uam5qRlRNaWNMZ1J5dkJSa0VmNVhCWnRUTkFTaWJoMEpWUDNld2lhY0dSRUV6QlRRMlBMRmJXZjl3c1ZUbkhWNUFuaHBLbFVKUFk4WkZmVWcvMA?x-oss-process=image/format,png" width="20"> |


##### 5.9.2 使用字符串常量池对字符串较多的场景减少内存占用案例
```
/**
 * 演示串池大小对性能的影响
 * -Xms500m -Xmx500m -XX:+PrintStringTableStatistics -XX:StringTableSize=1009
 * <p>
 * 字符串常量池默认桶数组大小为：60013，对字符串常量池调优主要是调节桶数据大小；如果字符串数量较多，则需要将此调大些，以减少查询复杂度（hash碰撞机率）
 */
 
public class T09_StringTableSizeForPerformance {
    public static void main(String[] args) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("in/linux.words"), "utf-8"))) {
            String line = null;
            long start = System.nanoTime();
            while (true) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                line.intern();
            }
            System.out.println("cost:" + (System.nanoTime() - start) / 1000000);
        }
    }
}
```
说明一下：in/linux.words 大约单词量在479829个，上述代码运行结果截图，如下：

| 读取大约48万单词  | 堆内存占用大小	 |       耗时 |
|:----|:--------:|----------:|
| 未放入字符串池  |  	约300兆  |    较短 |
| 放入字符串池  |  	约70兆   |       较长 |

<font color="#f33b45" size="3">运行结果1：未放入字符串常量池中，运行情况截图</font>

![字符串常量池对字符串较多的场景](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xbG9nby5jbi9tbWJpel9wbmcvN0lRUENidW1Uam5qRlRNaWNMZ1J5dkJSa0VmNVhCWnRUMlNnM0JpY1hTemVpYlM5OEN4TFVUd0VDeElyTUZpY2licm01Qnd2QjBZbEpCbTZkcFRQNmpEbEg2dy8w?x-oss-process=image/format,png)
![字符串常量池对字符串较多的场景](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xbG9nby5jbi9tbWJpel9wbmcvN0lRUENidW1Uam5qRlRNaWNMZ1J5dkJSa0VmNVhCWnRUb1prbzIyeHpVMmd6YkM4ejRFNFpIRGdGaWFLbnJzNWFvMmR0TmVqdGJvcVVaeVIxMlZpYnBJRFEvMA?x-oss-process=image/format,png)
<font color="#f33b45" size="3">运行结果2：放入字符串常量池中，运行情况截图</font>
![字符串常量池对字符串较多的场景](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xbG9nby5jbi9tbWJpel9wbmcvN0lRUENidW1Uam5qRlRNaWNMZ1J5dkJSa0VmNVhCWnRUWlJhOGlia0NXSlFzdW5xTlZWR1dhVXl4OUtkd1NzVURrRVdzZllpY3BpYWsxR1k2ckJnaWN3aWFnOGcvMA?x-oss-process=image/format,png)
![字符串常量池对字符串较多的场景](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9tbWJpei5xbG9nby5jbi9tbWJpel9wbmcvN0lRUENidW1Uam5qRlRNaWNMZ1J5dkJSa0VmNVhCWnRUbk9XU2xvOW1pYkVoSFRibERJRDNpYlZRR1ZxaWNaWTlRanc3bnBPT1JnZnkwa1RBbTlST2liUGtuZy8w?x-oss-process=image/format,png)

### 6、直接内存Direct Memory
#### 6.1 直接内存定义
- 常见于NIO操作时，用于数据缓冲
- 分配回收成本较高，但读写性能高
- 不受JVM内存回收管理
#### 6.2 原理讲解
- 普通内存
  - 需要从用户态向内核态申请资源，即用户态会创建一个java 缓冲区byte[]，内核态会创建系统缓冲区。
- 普通内存
  - 需要从用户态向内核态申请资源，即内核态会创建一块直接内存direct memory，这块direct memory内存可以在用户态、内核态使用。
  
<font color="#87cefa" size="3">通常使用内存（未使用直接内存） VS 直接内存，原理对比图</font><br />
文件读写流程
![未使用直接内存](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608150715.png)
使用了DirectBuffer
![直接内存](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20200608150736.png)
直接内存是操作系统和Java代码都可以访问的一块区域，无需将代码从系统内存复制到Java堆内存，从而提高了效率
##### 6.2.1 释放原理
<font size="3">直接内存的回收不是通过JVM的垃圾回收来释放的，而是通过**unsafe.freeMemory**来手动释放</font>
```
//通过ByteBuffer申请1M的直接内存
ByteBuffer byteBuffer = ByteBuffer.allocateDirect(_1M);
```
<font size="3">申请直接内存，但JVM并不能回收直接内存中的内容，它是如何实现回收的呢？</font>
**allocateDirect的实现**
```
public static ByteBuffer allocateDirect(int capacity) {
    return new DirectByteBuffer(capacity);
}
```
DirectByteBuffer类
```
DirectByteBuffer(int cap) {   // package-private
   
    super(-1, 0, cap, cap);
    boolean pa = VM.isDirectMemoryPageAligned();
    int ps = Bits.pageSize();
    long size = Math.max(1L, (long)cap + (pa ? ps : 0));
    Bits.reserveMemory(size, cap);

    long base = 0;
    try {
        base = unsafe.allocateMemory(size); //申请内存
    } catch (OutOfMemoryError x) {
        Bits.unreserveMemory(size, cap);
        throw x;
    }
    unsafe.setMemory(base, size, (byte) 0);
    if (pa && (base % ps != 0)) {
        // Round up to page boundary
        address = base + ps - (base & (ps - 1));
    } else {
        address = base;
    }
    cleaner = Cleaner.create(this, new Deallocator(base, size, cap)); //通过虚引用，来实现直接内存的释放，this为虚引用的实际对象
    att = null;
}
```
<font size="3" >这里调用了一个Cleaner的create方法，且后台线程还会对虚引用的对象监测，如果虚引用的实际对象（这里是DirectByteBuffer）被回收以后，就会调用Cleaner的clean方法，来清除直接内存中占用的内存</font>
```
public void clean() {
       if (remove(this)) {
           try {
               this.thunk.run(); //调用run方法
           } catch (final Throwable var2) {
               AccessController.doPrivileged(new PrivilegedAction<Void>() {
                   public Void run() {
                       if (System.err != null) {
                           (new Error("Cleaner terminated abnormally", var2)).printStackTrace();
                       }

                       System.exit(1);
                       return null;
                   }
               });
           }
```
<font size="3" >对应对象的run方法</font>
```
public void run() {
    if (address == 0) {
        // Paranoia
        return;
    }
    unsafe.freeMemory(address); //释放直接内存中占用的内存
    address = 0;
    Bits.unreserveMemory(size, capacity);
}
```
<font size="3" >**直接内存的回收机制总结**</font>
 - 使用了Unsafe类来完成直接内存的分配回收，回收需要主动调用freeMemory方法
 - ByteBuffer的实现内部使用了Cleaner（虚引用）来检测ByteBuffer。一旦ByteBuffer被垃圾回收，那么会由ReferenceHandler来调用Cleaner的clean方法调用freeMemory来释放内存


#### 6.3 直接内存与传统方式读取大文件耗时对比案例
<font size="3">接下来，我们将对一个大约1.29G大小的视频文件进行读取并写入指定文件中，即复制。代码如下：</font>
```
package com.jvm.t05_direct;
 
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
 
 
public class T01_IoVsDirectBuffer {
    static final String FROM = "E:\\Flink CEP.mp4";
    static final String TO = "E:\\a.mp4";
    static final int _1Mb = 1024 * 1024;
 
    public static void main(String[] args) {
        io(); 
        directBuffer(); 
    }
 
    private static void directBuffer() {
        long start = System.nanoTime();
        try (FileChannel from = new FileInputStream(FROM).getChannel();
             FileChannel to = new FileOutputStream(TO).getChannel();
        ) {
            ByteBuffer bb = ByteBuffer.allocateDirect(_1Mb);
            while (true) {
                int len = from.read(bb);
                if (len == -1) {
                    break;
                }
                bb.flip();
                to.write(bb);
                bb.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.nanoTime();
        System.out.println("directBuffer 用时：" + (end - start) / 1000_000.0);
    }
 
    private static void io() {
        long start = System.nanoTime();
        try (FileInputStream from = new FileInputStream(FROM);
             FileOutputStream to = new FileOutputStream(TO);
        ) {
            byte[] buf = new byte[_1Mb];
            while (true) {
                int len = from.read(buf);
                if (len == -1) {
                    break;
                }
                to.write(buf, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.nanoTime();
        System.out.println("io 用时：" + (end - start) / 1000_000.0);
    }
}
```

运行耗时对比表如下:

| 序 号  | 传统方式 IO |直接内存directBuffer |说明 |
| :------------- | :----------: | :----------: |------------: |
| 测试1 |   18871.591 ms   |  6335.745 ms  |没有缓存 |
| 测试2    |   5710.124 ms   |   5497.707 ms    |  有缓存|
| 测试3       |   7355.304 ms     |   5103.806 ms     |     有缓存|

#### 6.4 直接内存溢出案例
```
/**
 * 演示直接内存溢出 java.lang.OutOfMemoryError: Direct buffer memory
 */
 
public class T02_DirectOutOfMemory {
    static int _100Mb = 1024 * 1024 * 100;
 
    public static void main(String[] args) {
        List<ByteBuffer> list = new ArrayList<>();
        int i = 0;
        try {
            while (true) {
                ByteBuffer byteBuffer = ByteBuffer.allocateDirect(_100Mb);
                list.add(byteBuffer);
                i++;
            }
        } finally {
            System.out.println(i);
        }
        // 方法区是jvm规范， jdk6 中对方法区的实现称为永久代
        //                  jdk8 对方法区的实现称为元空间
    }
}
```

#### 6.5 分配和使用原理

<font color="#f33b45" size="3">  代码参考：com.jvm.t05_direct.T03_DirectMemoryGcBySystemGc</font>

```
/**
 * 禁用显式回收对直接内存的影响
 * <p>
 * 因为程序调用System.gc() 会触发full gc，可能会长时间在垃圾回收
 * <p>
 * 为了避免程序员显示调用System.gc(),  我们一般禁用显式调用System.gc()
 * 禁用显式System.gc()，会对直接内存有影响，为此，我们需要通过unSafe类的freeMemory()方法来释放直接内存
 */
 
public class T03_DirectMemoryGcBySystemGc {
 
    static int _1Gb = 1024 * 1024 * 1024;
 
    /*
     * -XX:+DisableExplicitGC 显式的
     */
    public static void main(String[] args) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(_1Gb);
        System.out.println("分配完毕...");
        System.in.read();
        System.out.println("开始释放...");
        byteBuffer = null;
        System.gc(); // 显式的垃圾回收，Full GC
        System.in.read();
    }
}
```
#### 6.6 分配和回收原理及案例演示
 - 使用了UnSafe对象完成直接内存的分配回收，并且回收需要主动调用freeMemory方法
 - ByteBuffer的实现类内部，使用了Cleaner（虚引用）来监测ByteBuffer对象，一旦ByteBuffer对象被垃圾回收，那么就会由ReferenceHandler线程通过Cleaner的clean方法调用freeMemory来释放直接内存
```
/**
 * 直接内存分配的底层原理：Unsafe
 *
 * 虚引用关联的对象被回收了，就会触发虚引用对象的clean方法，续而调用Unsafe的freeMemory() 方法
 *
 * 6.3 分配和回收原理
 *
 * 使用了UnSafe对象完成直接内存的分配回收，并且回收需要主动调用freeMemory方法
 *
 * ByteBuffer的实现类内部，使用了Cleaner（虚引用）来监测ByteBuffer对象，一旦ByteBuffer对象被垃圾回收，
 * 那么就会由ReferenceHandler线程通过Cleaner的clean方法调用freeMemory来释放直接内存
 */
 
public class T04_DirectMemoryGcByUnsafe {
    static int _1Gb = 1024 * 1024 * 1024;
 
    public static void main(String[] args) throws IOException {
        Unsafe unsafe = getUnsafe();
        // 分配内存
        long base = unsafe.allocateMemory(_1Gb);
        unsafe.setMemory(base, _1Gb, (byte) 0);
        System.in.read();
 
        // 释放内存
        unsafe.freeMemory(base);
        System.in.read();
    }
 
    public static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            Unsafe unsafe = (Unsafe) f.get(null);
            return unsafe;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
```

###  7、垃圾回收
#### 7.11
