import java.io.InputStream.*;
import java.io.OutputStream.*;
import java.lang.*;
import java.net.*;
import java.lang.reflect.*;
import java.io.*;

public class ThreadRecv extends Thread {
  private String hostname;
  private int port;
  private int PacketSize;
  private boolean protocol;	// 1 -- TCP ; 0 -- UDP
  private boolean bRunning;
  private byte[] Buffer;
  private InetAddress addr;
  private ServerSocket TCPAcceptServer;
  private DatagramSocket UDPServer;
  private int seqno;
  private long starttime;
  private boolean bquit;
  private int PacketRecv;
  private int LastRecv;	// no. of last bytes received
  private boolean File;
  private String filep;
  private FileOutputStream f2;
  private BufferedOutputStream output;
  private Socket TCPClient;
  private DatagramSocket UDPClient;
   	private int Ack;	  
		

  public ThreadRecv(String localhostname, int localport, int iPacketSize, boolean iProtocol, String filename, String Rfile) {
	
	if (iProtocol)
	{
		try {
		addr = InetAddress.getByName(localhostname);
		TCPClient = new Socket(addr, localport);	// assume backlog = 1
		
		PrintWriter pw = new PrintWriter( TCPClient.getOutputStream() );
		
		pw.write( Rfile, 0, Rfile.length());
		pw.flush();
		
		System.out.println(Rfile);
		
		 } catch (Exception e) { JMessageBox ErrBox = new JMessageBox(e.getMessage(), "JNetProbe Error");
	                              ErrBox.show();
	                              bRunning = false; }
	                              
	        int amount2=0;
    		int offset2 =0;
    		byte[] ackbuf= new byte[100];
 	
	        
	        
// 		while( amount2 < 100 )
//    		{

	try{
		int	n= TCPClient.getInputStream().read( ackbuf, 0, 100 );
	}catch( IOException e) {}
//			amount2 +=n;
//			offset2 =amount2;
//		}
 
		Ack=0; 	
 	
 		for (int i = 0; i < 4; i++) {
            		int temp = 255;
            		temp &= ackbuf[i];
            		Ack += (temp << 8*i);
        	}
        	if( Ack==-300 )
 			System.out.println("			---File is found--- ");
		else	System.out.println("			---File is NOT found--- ");
 
 
	                              
	                              
	}
	else
	{
		
		try{
			addr = InetAddress.getByName(localhostname);
			//UDPClient = new DatagramSocket(localport, addr);
			UDPClient = new DatagramSocket();
		
		String sentence = Rfile;
		byte[] sendData = new byte[1024];
		sendData = sentence.getBytes();
				
		DatagramPacket dp = new DatagramPacket( sendData, sendData.length, addr, localport);
		
		UDPClient.send( dp );
		
		}catch (Exception e) { JMessageBox ErrBox = new JMessageBox(e.getMessage(), "JNetProbe Error");
	                              ErrBox.show();
	                              bRunning = false; }
		
		byte[] buf2 = new byte[100];
 		DatagramPacket IDPacket = new DatagramPacket( buf2, buf2.length );
	try{ 	
 		UDPClient.receive( IDPacket );
 	}catch( IOException e) {}
 	
 		if( IDPacket.getLength() != 0 )
	 	{
/* 			//playerID =);
 			for (int i = 0; i < 4; i++) {
            			int temp = 255;
	            		temp &= buf2[i];
        	    		playerID += (temp << 8*i);
        		}
	 		System.out.println("Player ID: "+playerID);
*/ 		}
	 	else System.out.println("not receive ID");
 	
 	
 		byte[] buf3 = new byte[100];
	 	DatagramPacket AckPacket = new DatagramPacket( buf3, buf3.length );
 	try{
 		UDPClient.receive( AckPacket );
 	}catch( IOException e) {}
 	
 		//String Ack = new String();
 		Ack=0;
 	
 		if( AckPacket.getLength() != 0 )
 		{
 			//playerID =);
 			for (int i = 0; i < 4; i++) {
            			int temp = 255;
            			temp &= buf3[i];
            			Ack += (temp << 8*i);
        		}
        		if( Ack==-300 )
 				System.out.println("			---File is found--- ");
			else	System.out.println("			---File is NOT found--- ");
 		}
 		else System.out.println("not receive Ack");

		
	}
      
      bRunning = false;
      bquit = false;
//      starttime = System.currentTimeMillis();
      File=true;
      PacketRecv = 0;
      seqno = 0;
      hostname = new String(localhostname);
      port = localport;
      filep = filename;
      
      if (filename.length()!=0)
      {
          filep = filename;
          File = true;
      }
                
      if (iPacketSize < 8) {
          JMessageBox ErrBox = new JMessageBox("Packet Size should be equal to or larger than 8!", "JNetProbe Error");
          ErrBox.show();
          return;
      }
      PacketSize = iPacketSize;
      protocol = iProtocol;
      bRunning = true;
      Buffer = new byte[PacketSize];
      
/*      try {
          addr = InetAddress.getByName(hostname);
          if (protocol)
              TCPAcceptServer = new ServerSocket(port, 1, addr);	// assume backlog = 1
          else
              UDPServer = new DatagramSocket(port, addr);
      } catch (Exception e) { JMessageBox ErrBox = new JMessageBox(e.getMessage(), "JNetProbe Error");
                              ErrBox.show();
                              bRunning = false; }
  */
      bquit = true;
      LastRecv = 0;
  }

  public void run() {
          RecvData();
  }

  public void RecvData() {
        if (protocol)
              TCPRecv();
        else
              UDPRecv();
  }

  public void TCPRecv() {
        try {
   //         Socket TCPServer = TCPAcceptServer.accept();
            if (bRunning) {
               bquit = false;
       //        starttime = System.currentTimeMillis();
               
               if (File)
               {
                   try {
        		f2 = new FileOutputStream( filep );
			output= new BufferedOutputStream(f2);
			
//			while( ( charsRead = input.read(b)) >-1 )
//			{
//				output.write(b, 0, charsRead);
//			}
                   } catch( IOException ioe) {ioe.printStackTrace();}
                }
               
               int lNumRecv=0;
//               String temp = new String();
               
               while(bRunning) {
//                   synchronized(this) {
                   
//                    int read = TCPClient.getInputStream().read(Buffer, LastRecv, PacketSize - LastRecv);
		    int read = TCPClient.getInputStream().read(Buffer, 0, PacketSize);
                    if (read!=-1)
                    {
                    	output.write(Buffer,0,read);
                    
                    /*                    
   //                 System.out.println(read);
                    
                    if (read >= 0) {
        //                if (LastRecv < 8 && (LastRecv+read) >= 8 && !AnalystPacket())
          //                  break;
                        LastRecv += read;
    //                    temp = Buffer.toString();
                     
                        if (File && read>=0)
                        {
              //              if (LastRecv >=8)
                            {
                 //               if (lNumRecv ==0) {
                 //                   output.write(Buffer,8,read-8);
       //                             System.out.println("File Written (1)");
                //                } else {
                                    output.write(Buffer,lNumRecv,read);
         //                           System.out.println("File Written (2)");
                 //               }
                            }
                        }                   
                        lNumRecv += read;                   
                        
                        if (!(lNumRecv < PacketSize))                        
                            lNumRecv=0;
                                                
                        LastRecv %= PacketSize;
                      */                          
                    }
                    else
                        break;
//                   }
              }
              TCPClient.getInputStream().close();
              TCPClient.close();
              bquit = true;
              QuitRecv();
            }
        } catch (Exception e) { if (bRunning && bquit)  {
                                      JMessageBox ErrBox = new JMessageBox("TCP Receive fail!!\r\n"+e.getMessage(), "JNetProbe Error");
                                      ErrBox.show();
                                      QuitRecv(); } }
  }

  public void UDPRecv() {
        try {
              DatagramPacket datagrambuf = new DatagramPacket(Buffer, Buffer.length);
              
                 if (File)
               {
                   try {
        		f2 = new FileOutputStream( filep );
			output= new BufferedOutputStream(f2);
			
//			while( ( charsRead = input.read(b)) >-1 )
//			{
//				output.write(b, 0, charsRead);
//			}
                   } catch( IOException ioe) {ioe.printStackTrace();}
                }
              
  //            static Object Lock = new Object();
              
              while (bRunning) {
                  synchronized(this) {
                  UDPClient.receive(datagrambuf);
//                  System.out.println(datagrambuf.getLength());
     //             if (PacketRecv == 0)
       //               starttime = System.currentTimeMillis();
                  bquit = false;
                  if (datagrambuf.getLength() >= 8 && !AnalystPacket())
                     break;
   //               System.out.println("After Break");
//   		System.out.println(seqno);
                  if (File)
                    output.write(datagrambuf.getData(), 8 ,datagrambuf.getLength()-8 );
                    
//               output.flush();
 //                 System.out.println("After Write");
     //           byte[] b = new byte[datagrambuf.getLength()];
                 }
       //         b=datagrambuf.getData();
       //           output.write(b, 8 ,getLength(b)-8 );
              }
              
         //     QuitRecv();
              
        } catch (Exception e) { if (bRunning && bquit) {
                                      JMessageBox ErrBox = new JMessageBox("UDP Receive fail!!\r\n"+e.getMessage(), "JNetProbe Error");
                                      ErrBox.show();
                                      QuitRecv(); }}
  }

  private boolean AnalystPacket() {
        int tempseqno = seqno;
        seqno = 0;
        for (int i = 0; i < 4; i++) {
            int temp = 255;
            temp &= Buffer[i];
            seqno += (temp << 8*i);
        }
        if (seqno == -100)
        {
        	System.out.println(seqno);
        //	bRunning = false;
  //      	bquit = true;
        	QuitRecv();
        	return false;
        	
        }
        
   //     System.out.println(seqno);
        
        if (tempseqno > seqno) {
            PacketRecv = 0;
            starttime = System.currentTimeMillis();
        }
        int pktsize = 0;
        for (int i = 0; i < 4; i++) {
            int temp = 255;
            temp &= Buffer[i+4];
            pktsize += (temp << 8*i);
        }
        if (pktsize != PacketSize) {
     //       JMessageBox ErrBox = new JMessageBox("Receive File ..... Finished!");
       //     ErrBox.show();
            return false;
        }
        PacketRecv++;
        return true;
  }

  public void QuitRecv() {
      if (bRunning || bquit) {
          if (protocol) {
            if (bquit) {
                try {
                    output.flush();
                    output.close();
                    f2.close();
                    TCPAcceptServer.close();
                } catch (Exception e) { JMessageBox ErrBox = new JMessageBox(e.getMessage(), "JNetProbe Error");
                                        ErrBox.show(); }
            }
          }
          else {
              if (bRunning) {
             	  UDPClient.close();
                  bquit = true;
              }
          }
          bRunning = false;
          
          if (File){          
          try {
              output.flush();
               output.close();
               System.out.println("File Closed");
               f2.close();
                   } catch (Exception e) { JMessageBox ErrBox = new JMessageBox(e.getMessage(), "JNetProbe Error");
                                        ErrBox.show(); }
          }
      }
}

  public int getpackettransferred() {
      return seqno+1;
  }

  public double getdatarate() {
      if ((System.currentTimeMillis() - starttime) != 0)
          return ((double) PacketRecv*PacketSize*1000/(System.currentTimeMillis() - starttime));
      else
          return ((double) PacketRecv*PacketSize*1000);
  }

  public boolean isReceiving() {
      return (bRunning && !bquit);
  }

  public boolean isRunning() {
      return bRunning;
  }

  public double getTimeElapsed() {
      return ((double) (System.currentTimeMillis() - starttime)/1000);
  }

  public int getpacketloss() {
      return (seqno+1) - PacketRecv;
  }

  public int getlossrate() {
      return ((int) 100*((seqno+1) - PacketRecv)/(seqno+1));
  }
}