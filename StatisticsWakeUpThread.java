import java.lang.*;

public class StatisticsWakeUpThread extends Thread {
  private long iRefreshInterval;
  private Runnable UpdateCode;
  private boolean bToQuit;

  public StatisticsWakeUpThread(int refresh_interval, Runnable update_code) {
        iRefreshInterval = refresh_interval;
        UpdateCode = update_code;
        bToQuit = false;
  }

  public void QuitUpdate () {
        bToQuit = true;
  }

  public void run () {
        while (!bToQuit) {
              javax.swing.SwingUtilities.invokeLater(UpdateCode);
              try {
                    sleep(iRefreshInterval);
              } catch (Exception e) {System.out.println(e.toString()); }
        }
  }
}