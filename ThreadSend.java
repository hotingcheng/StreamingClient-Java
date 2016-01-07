
import java.lang.*;
import java.net.*;
import java.io.*;

public class ThreadSend extends Thread {
  private String hostname;
  private int port;
  private int PacketSize;
  private int SendRate;
  private int NumToSend;
  private boolean protocol;
  private boolean bSendForever;
  private byte[] Buffer;
  private InetAddress addr;
  private Socket TCPClient;
  private DatagramSocket UDPClient;
  private int seqno;
  private long starttime;

  public ThreadSend(String peerhostname, int peerport, int iPacketSize, int iSendRate, int iNumToSend, boolean iProtocol) {
      NumToSend = 0;
      bSendForever = false;
      starttime = System.currentTimeMillis();
      seqno = 0;
      hostname = new String(peerhostname);
      port = peerport;
      
      if (iPacketSize < 8) {
          JMessageBox ErrBox = new JMessageBox("Packet Size should be equal to or larger than 8!", "JNetProbe Error");
          ErrBox.show();
          return;
      }
      
      if (iSendRate < 0) {
          JMessageBox ErrBox = new JMessageBox("Sending Rate should not be a negative number!", "JNetProbe Error");
          ErrBox.show();
          return;
      }
      
      if (iNumToSend < 0) {
          JMessageBox ErrBox = new JMessageBox("Packet Number should not be a negative number!", "JNetProbe Error");
          ErrBox.show();
          return;
      }
      
      PacketSize = iPacketSize;
      SendRate = iSendRate;
      NumToSend = iNumToSend;      
      protocol = iProtocol;
      bSendForever = (NumToSend == 0 ? true : false);
      Buffer = new byte[PacketSize];
      try {
          addr = InetAddress.getByName(hostname);
          if (protocol)
              TCPClient = new Socket(addr, port);
          else
              UDPClient = new DatagramSocket();
      } catch (Exception e) { JMessageBox ErrBox = new JMessageBox("Fail to connect host ["+hostname+":"+port+"]!\r\n"+e.getMessage(), "JNetProbe Error");
                              ErrBox.show();
                              bSendForever = false;
                              NumToSend = 0; }
  }

  public void run() {
          SendData();
  }

  public void SendData() {
      for (int i = 0; i < Buffer.length; i++)
          Buffer[i] = 0;
      for (int i = 0; i < 4; i++)
          Buffer[i+4] |= PacketSize >> (i*8);
      Send();
  }

  public void Send() {
      seqno = 0;
      int iSendDelay = 0;
      if (SendRate != 0)
	  iSendDelay = PacketSize*1000/SendRate;

      while (NumToSend > seqno || bSendForever) {
          for (int i = 0; i < 4; i++) {
              Buffer[i] = 0;
              Buffer[i] |= seqno >> (i*8);
          }

          if (protocol) {
              try {
                  TCPClient.getOutputStream().write(Buffer);
              } catch (Exception e) { JMessageBox ErrBox = new JMessageBox("TCP send fail!!\r\n"+e.getMessage(), "JNetProbe Error");
                                      ErrBox.show();
                                      QuitSend(); }
          }
          else {
              try {
                  DatagramPacket datagrambuf = new DatagramPacket(Buffer, Buffer.length, addr, port);
                  UDPClient.send(datagrambuf);
              } catch (Exception e) { if (NumToSend > seqno || bSendForever) {
                                            JMessageBox ErrBox = new JMessageBox("UDP send fail!!\r\n"+e.getMessage(), "JNetProbe Error");
                                            ErrBox.show();
                                            QuitSend(); }}
          }

          long tosleep = (long)(iSendDelay*seqno - (System.currentTimeMillis() - starttime));
          try {
	      if (tosleep > 0)
                  sleep(tosleep);
          } catch (Exception e) { System.out.println(e.toString()); }
          seqno++;
      }
      QuitSend();
  }

  public void QuitSend() {
      if (bSendForever || NumToSend != 0) {
          bSendForever = false;
          NumToSend = 0;
          if (protocol) {
              try {
                  TCPClient.getOutputStream().close();
                  TCPClient.close();
              } catch (Exception e) { JMessageBox ErrBox = new JMessageBox(e.getMessage(), "JNetProbe Error");
                                      ErrBox.show(); }
          }
          else
              UDPClient.close();
      }
  }

  public int getpackettransferred() {
      return seqno+1;
  }

  public double getdatarate() {
      if ((System.currentTimeMillis() - starttime) != 0)
          return ((double) (seqno+1)*PacketSize*1000/(System.currentTimeMillis() - starttime));
      else
          return ((double) (seqno+1)*PacketSize*1000);
  }

  public boolean isSending() {
      return (NumToSend > seqno || bSendForever);
  }

  public double getTimeElapsed() {
      return ((double) (System.currentTimeMillis() - starttime)/1000);
  }
}