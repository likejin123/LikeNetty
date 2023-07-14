package com.likejin.nio;


/*
scattering： 将数据写入到buffer时，可以采用buffer数组，依次写入（一个buffer满了，再写另一个）分散
gathering：从buffer读取数据时可以采用buffer数组，依次将这些buffer数组读出
 **/

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * @Author 李柯锦
 * @Date 2023/7/13 17:34
 * @Description buffer和channel的分散和聚集
 */
public class ScatteringAndGatheringTest {

    public static void main(String[] args) throws Exception {
        //使用ServerSocketChannel 和 SocketChannel

        //一个线程对应一个serverSocketChannel 并且一个serverSocketChannel可以获取一个客户端连接channel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);

        //绑定端口到socket并启动
        serverSocketChannel.socket().bind(inetSocketAddress);

        //创建buffer数组
        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        byteBuffers[0] = ByteBuffer.allocate(5);
        byteBuffers[1] = ByteBuffer.allocate(3);

        //等待客户端连接(这里的SocketChannel为通道，与buffer连接)
        SocketChannel socketChannel = serverSocketChannel.accept();

        int messageLenth = 8;//假定从客户端接受8个字节

        //循环读取
        while (true){
            int byteRead = 0;

            while (byteRead < messageLenth ){
                //channel数据读入byteBuffer
                long read = socketChannel.read(byteBuffers);
                byteRead += read; //累计读取的字节数
                System.out.println("读取到的字节数" + byteRead );
                //使用流打印  看看当前buffer的position和limit

                Arrays.asList(byteBuffers).stream()
                        .map(buffer -> "postion=" + buffer.position() + "," +"limit = " + buffer.limit())
                        .forEach(System.out::println);
            }


            //将所有的buffer进行翻转
            Arrays.asList(byteBuffers).forEach(buffer -> buffer.flip());

            //将数据读出显示到客户端
            long byteWrite = 0;
            while (byteWrite < messageLenth){
                //将用channel读出byteBuffer数据
                long write = socketChannel.write(byteBuffers);
                byteWrite += write;
            }


            //将所有buffer进行clear
            Arrays.asList(byteBuffers).forEach(buffer -> buffer.clear());

            System.out.println("byteRead" + byteRead + "bytewrite" + byteWrite);
        }

    }
}
