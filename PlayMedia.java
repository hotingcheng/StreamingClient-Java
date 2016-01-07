import javax.media.*;
import com.sun.media.ui.*;
import javax.media.protocol.*;
import javax.media.protocol.DataSource;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.Vector;
import java.util.StringTokenizer.*;
import java.beans.PropertyVetoException;

public class PlayMedia extends Frame {

    /*************************************************************************
     * MAIN PROGRAM / STATIC METHODS
     *************************************************************************/
/*    
    public static void main(String args[]) {
	PlayMedia mdi = new PlayMedia();
    }
*/
    static void Fatal(String s) {
	MessageBox mb = new MessageBox("JMF Error", s);
    }    

    /*************************************************************************
     * VARIABLES
     *************************************************************************/
    
    JMFrame jmframe = null;
    JDesktopPane desktop;
    FileDialog fd = null;
    CheckboxMenuItem cbAutoLoop = null;
    Player player = null;
    Player newPlayer = null;
    MediaLocator media_loc = null;
    String filename;
    
        
    /*************************************************************************
     * METHODS
     *************************************************************************/
    
    //public PlayMedia(InputStream is, String filestring) {
    public PlayMedia( MediaLocator a ) {
	super("Java Media Player");
	media_loc = a;
	// Add the desktop pane
	setLayout( new BorderLayout() );
	desktop = new JDesktopPane();
	desktop.setDoubleBuffered(true);
	add("Center", desktop);
//	setMenuBar(createMenuBar());
	setSize(390, 370);
	setLocation(355,25);	
	setVisible(true);
	//filename = filestring;
	
	try {
	    UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
	} catch (Exception e) {
	    System.err.println("Could not initialize java.awt Metal lnf");
	}
	addWindowListener( new WindowAdapter() {
	    public void windowClosing(WindowEvent we) {
	    	if( jmframe != null )
	    	{
	    		jmframe.stop();
	    	}
	    	dispose();
//		System.exit(0);
	    }
	} );

	Manager.setHint(Manager.LIGHTWEIGHT_RENDERER, new Boolean(true));

	//openFile(is);
	openFile(a);
	}

/*	
	FileInputStream f1=null;
//////////////////////	test
	try{
		byte[] b = new byte[60];
		int charsRead =0;
			
		f1 = new FileInputStream( "shit.mpg" );
		
	
	}
	catch( IOException ioe)
	{
		System.out.println("Error "+ioe);
	}
		openFile(f1);
	
    }
*/
    /**
     * Open a media file.
     */
    //public void openFile(InputStream is) {
    public void openFile(MediaLocator a )
    {
   // 	String mediaFile = filename;
	Player player = null;
	
    	try{
    	
    		Vector prefixes = PackageManager.getProtocolPrefixList();
            String mediaPlayerPrefix = new String("ieg4180");
            //prefixes.addElement(mediaPlayerPrefix);
            prefixes.add(0, mediaPlayerPrefix);
 
            for (java.util.Enumeration e = prefixes.elements(); e.hasMoreElements(); ){
                System.out.println((String) e.nextElement());
            }
            PackageManager.setProtocolPrefixList(prefixes);
           PackageManager.commitProtocolPrefixList();
           
    	
//            CustomDataSource ds = new CustomDataSource(is, "shit.mpg");
		
            //CustomDataSource ds = new CustomDataSource(is, filename);
//            CustomDataSource ds = new CustomDataSource(localhostname, localport, iPacketSize, iProtocol, filename);
// 	System.out.println(filename);

            System.out.println("Creating Player");
//            player = Manager.createPlayer(new MediaLocator(new URL(getDocumentBase(),"test.mpg").toExternalForm()));
		DataSource ds = Manager.createDataSource(a);
            player = Manager.createPlayer(ds);
            System.out.println("1");
//            player.addControllerListener(this);
//            player.	);
//            player.start();                           
 
        }
//        catch (CannotRealizeException e) { System.out.println(e);}
        catch (NoDataSourceException e) { System.out.println(e);}
        catch (NoPlayerException e){System.out.println(e);}
        catch (IOException e){System.out.println(e);}
        //validate();
    	
	if (player != null) {
//	    this.filename = filename;
	    jmframe = new JMFrame(player, "");
	    desktop.add(jmframe);
	}
    }
}
/*


class JMFrame extends JInternalFrame implements ControllerListener {
    Player mplayer;
    Component visual = null;
    Component control = null;
    int videoWidth = 0;
    int videoHeight = 0;
    int controlHeight = 30;
    int insetWidth = 10;
    int insetHeight = 30;
//  int controlHeight = 0;
//  int insetWidth = 0;
//  int insetHeight = 0;
    boolean firstTime = true;
    
    public JMFrame(Player player, String title) {
    	
	super(title, true, false, true , true);
	System.out.println("2");
	getContentPane().setLayout( new BorderLayout() );
//	setSize(320, 320);
//	setSize(500, 510);
	setLocation(50,50);
//etLocation(0, -30);
	setVisible(true);
	mplayer = player;
	mplayer.addControllerListener((ControllerListener) this);
        System.out.println("before mplayer realize");
	mplayer.realize();
        System.out.println("after mplayer realize");		
	addInternalFrameListener( new InternalFrameAdapter() {
	    public void internalFrameClosing(InternalFrameEvent ife) {
		mplayer.close();
	    }
	} );
		    
    }
    
    public void controllerUpdate(ControllerEvent ce) {
	if (ce instanceof RealizeCompleteEvent) {
            System.out.println("pre-fetch");
	    mplayer.prefetch();
	} else if (ce instanceof PrefetchCompleteEvent) {
		System.out.println("pre-fetch completed");		
	    if (visual != null)
		return;
	    
	    if ((visual = mplayer.getVisualComponent()) != null) {
		Dimension size = visual.getPreferredSize();
		videoWidth = size.width;
		videoHeight = size.height;
		getContentPane().add("Center", visual);
	    } else
		videoWidth = 320;
	    if ((control = mplayer.getControlPanelComponent()) != null) {
		controlHeight = control.getPreferredSize().height;
		getContentPane().add("South", control);
	    }
	    setSize(videoWidth + insetWidth,
		    videoHeight + controlHeight + insetHeight);
//	    setSize(500, 500);
//	   setSize(390, 400);
   	try{
		this.setMaximum(true);
		this.setResizable(false);
	   }catch( PropertyVetoException pe ) {}
	

	    validate();
	System.out.println("mplayer starts normally");
	    mplayer.start();
	} else if (ce instanceof EndOfMediaEvent) {
	    mplayer.setMediaTime(new Time(0));
            System.out.println("mplayer starts");
	    mplayer.start();
	}
    }
    
    public void stop()
    {
    	System.out.println("Internal Frame dispose");
        mplayer.close();
        this.dispose();
    }
}
          
          
*/          

/*
//socket connection
	 System.out.println("connect");      
       if (controlSocket != null)
       {
          disconnect();
       }
       // extract FTP server name and target filename from locator
//       parseLocator();   
       controlSocket = new Socket(host, port);
       System.out.println("Connection established");
       
//       controlOut = new PrintStream(new BufferedOutputStream(
//          controlSocket.getOutputStream()), true);
       controlIn = new
          BufferedInputStream(controlSocket.getInputStream());
*/          