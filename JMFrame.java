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
//	    mplayer.start();
	}
    }
    
    public void stop()
    {
    	System.out.println("Internal Frame dispose");
        mplayer.close();
        this.dispose();
    }
}
          
          