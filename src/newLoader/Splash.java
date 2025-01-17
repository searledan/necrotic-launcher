package newLoader;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
 
@SuppressWarnings("serial")
public class Splash extends JFrame {
	private static String downloadUrl = "http://rsps.dansearle.cloud/client/client.jar";
	private static String fileName = "Client.jar";
	private static String serverName = "Necrotic";
	private static String backgroundImageUrl = "http://rsps.dansearle.cloud/images/0.png";
	private static String saveDirectory = "Clients";
	private static String currentLoaderVersion = "http://rsps.dansearle.cloud/loader/loader_version.txt";
	private static double loaderVersion = 5.0;
	private static String loaderUrl = "http://rsps.dansearle.cloud/loader";
	public static URL url;
    private JLabel imglabel;
    private ImageIcon img;
    private static JProgressBar pbar;
    Thread t = null;
    
	public double getNewestLoaderVersion() {
		try {
			URL tmp = new URL(currentLoaderVersion);
			BufferedReader br = new BufferedReader(new InputStreamReader(tmp.openStream()));
			return Double.parseDouble(br.readLine());
		} catch (Exception e) {
			return -1D;
		}
	}
	
	public double getCurrentLoaderVersion() {
		return loaderVersion;
	}


	public static String getLoaderDirectory() {
		String loaderLoc = System.getProperty("user.home") + "/Necrotic/";
		loaderLoc += saveDirectory + "/";
		File settingDir = new File(loaderLoc);
		if(!settingDir.exists()) {
			settingDir.mkdirs();
		}
		return loaderLoc;
	}

    public Splash() {
        super(serverName+" "+"Splash");
        
        File file = new File(getLoaderDirectory() + fileName);

		try {
			url = new URL(downloadUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
        setSize(543, 391);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        
        try {
			img = new ImageIcon(new URL(backgroundImageUrl));
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
        
//        imglabel = new JLabel(img);
//        add(imglabel);
        setLayout(null);
//        pbar = new JProgressBar();
//        pbar.setMinimum(0);
//        pbar.setMaximum(100);
//        pbar.setStringPainted(true);
//        pbar.setForeground(Color.LIGHT_GRAY);
//        imglabel.setBounds(0, 0, 543, 391);
//        add(pbar);
//        pbar.setPreferredSize(new Dimension(310, 30));
//        pbar.setBounds(70, 320, 404, 20);
		setBackground(new Color(1.0f,1.0f,1.0f,0.0f));

		try {
        	if (getNewestLoaderVersion() > getCurrentLoaderVersion()) {
//        		pbar.setString("A new loader is available. Please download the latest loader.");
        		if(Desktop.isDesktopSupported()) {
					System.out.println("Attempting to open browser to: "+loaderUrl);
				} else {
					System.out.println("Desktop unsupported! Attempting other procedure to open: "+loaderUrl);
		            Runtime runtime = Runtime.getRuntime();
		            try {
		                runtime.exec("xdg-open " + url);
		            } catch (IOException e) {
		                e.printStackTrace();
					}
				}
			}
        } catch (Exception e) {
        	e.printStackTrace();
        }


        try {
            if (file.exists()) {
            	URLConnection connection = url.openConnection();
            	connection.connect();
    			long time = connection.getLastModified();
    			if (time > file.lastModified()) {
                    if (!startDialogue()) {
                    	startApplication();
                        return;
                    }
    			} else {
                    setVisible(true);
                    Thread.sleep(3000);
                    startApplication();
                    return;
                }
    		}
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Thread t = new Thread() {
 
            public void run() {
            	OutputStream dest = null;
            	URLConnection download;
            	InputStream readFileToDownload = null;
            	try {
            		dest = new BufferedOutputStream(new FileOutputStream(file));
            		download = url.openConnection();
            		readFileToDownload = download.getInputStream();
            		byte[] data = new byte[1024];
            		int numRead;
            		long numWritten = 0;
            		int length = download.getContentLength();
            		while ((numRead = readFileToDownload.read(data)) != -1) {
            			dest.write(data, 0, numRead);
            			numWritten += numRead;
            			int percent = (int)(((double)numWritten / (double)length) * 100D);
//            			pbar.setValue(percent);
//            			pbar.setString(""+(percent < 99 ? "Downloading "+serverName+" - "+percent+"%" : "Complete")+"");
            		}
            	} catch (Exception exception) {
            		exception.printStackTrace();
            	} finally {
            		try {
            			if (readFileToDownload != null)
            				readFileToDownload.close();
            			if (dest != null)
            				dest.close();
            			Thread.sleep(1000L);
            			startApplication();
            		} catch (IOException | InterruptedException ioe) {
            				
            		}
            	}
            }
        };
        t.start();
    }
    
    public boolean startDialogue() {
        setVisible(true);
        int selection = JOptionPane.showConfirmDialog(null, "An update is available. Do you wish to download?", "Update Available", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        return selection == JOptionPane.OK_OPTION;
    }


    /**
     * Launches the downloaded Jar file and closes the progress bar
     */
    public static void startApplication() {
    	try {
			Runtime.getRuntime().exec("java -jar "+(getLoaderDirectory() +  fileName)+"");
			Thread.sleep(1000L);
			System.exit(0);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
    }


}
