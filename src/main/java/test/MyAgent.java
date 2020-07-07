package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.instrument.Instrumentation;
import java.net.ServerSocket;
import java.net.Socket;

public class MyAgent {
	public static void premain(String args, Instrumentation inst){
        System.out.println("Hi, I'm agent!");
        System.out.println("args:"+args); // myagent=1,a=2,b=3
        inst.addTransformer(new MyTransformer());
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
		        	ServerSocket server = new ServerSocket(8765);
		        	System.out.println("=========socket server启动了，javaagent支持=========");
		        	while(true) {
		        		Socket socket = server.accept();
		        		InputStream is = socket.getInputStream();// 字节输入流            
		        		InputStreamReader isr = new InputStreamReader(is);// 将字节流转为            
		        		BufferedReader br = new BufferedReader(isr);// 为输入流添加缓冲    
		        		StringBuilder sb = new StringBuilder();
		        		String info = null;
		        		while ((info = br.readLine()) != null) {
		        			System.out.println("我是服务器，客户端说" + info);
		        			sb.append(info);
		        		}
		        		socket.shutdownInput();// 关闭输入流 
		        		OutputStream os = socket.getOutputStream(); // 4.获取输出流   
		        		PrintWriter pw = new PrintWriter(os); // 包装打印流       
		        		// 
		        		if("chazhuang.txt".equals(sb.toString())) {
		        			// 获取插桩文件
		        			String gen = System.getProperty("user.dir");
		        			BufferedReader reader = new BufferedReader(new FileReader(new File(gen+"/chazhuang.txt")));
		        	        String tempStr = null;
		        	        while ((tempStr = reader.readLine()) != null) {
		        	        	pw.write(tempStr+"\n");
		                		pw.flush();
		        	        }
		        	        reader.close();
		        		}
		        		socket.shutdownOutput();
		        		// 5.关闭资源            
		        		pw.close();
		        		br.close();
		        		isr.close();
		        		is.close();
		        		//server.close();
		        	}
		        } catch (IOException e) {
	
		        }finally {
		        	
		        }
			}
		}).start();
    }
}
