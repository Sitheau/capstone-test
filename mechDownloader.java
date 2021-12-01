import java.net.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class mechDownloader {

    public static void install() { //install to preset directory
        try {
            URL assetsUrl = new URL("https://github.com/Sitheau/capstone-test/releases/latest/download/mech.zip"); //TODO: change back to mech repo not my repo lolllllll
            HttpURLConnection connection = (HttpURLConnection) assetsUrl.openConnection();
            connection.setRequestProperty("Accept", "application/octet-stream");
            ReadableByteChannel uChannel = Channels.newChannel(connection.getInputStream());
            String userDir = System.getProperty("user.home") + "\\Downloads";
            FileOutputStream foStream = new FileOutputStream(userDir + "\\mech.zip"); //default installs to user documents
            FileChannel fChannel = foStream.getChannel();
            fChannel.transferFrom(uChannel, 0, Long.MAX_VALUE);
            uChannel.close();
            foStream.close();
            fChannel.close();
        } catch (Exception E) {
            throw new RuntimeException(E);
        }
 
    }

    public static void delete(File folder) throws IOException{ //recursively delete folders contents then delete folder in documents, be careful :^)
        if (folder.isDirectory()) {
            for (File sub : folder.listFiles()) {
                delete(sub);
            }
        }
        folder.delete();
    }

    private static void unpack(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println("Unzipping to "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
            File deleteZip = new File(zipFilePath);
            delete(deleteZip);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void build(double verNum) throws IOException  { //should run mech.exe broken C:\\Users\\nicca\\Documents\\Capstone\\mech_installer2
        List<String> args = new ArrayList<String>();
        args.add(System.getProperty("user.home") + "\\Downloads\\mech\\mechv0."+verNum+".exe");
        ProcessBuilder pb = new ProcessBuilder(args);
        pb.start();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        URL tagUrl = new URL("https://api.github.com/repos/sitheau/capstone-test/tags"); //url for all tags

        HttpURLConnection c = (HttpURLConnection) tagUrl.openConnection(); //GET JSON for all tags
        c.setRequestMethod("GET");
        c.setRequestProperty("Content-length", "0");
        c.setUseCaches(false);
        c.setAllowUserInteraction(false);
        c.connect();
        int status = c.getResponseCode();

        switch (status) {
            case 200:
            case 201:
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream())); //success? read JSON body
                StringBuilder sb = new StringBuilder();
                sb.append(br.readLine());
                br.close();

                String[] vTemp = sb.toString().split("\"", 5); //weird regex to get the latest version 
                String[] verNum = vTemp[3].split("v0."); //get just the last 2 numbers out of v0.x.x
                double verDouble = Double.parseDouble(verNum[1].trim()); //convernt to double to compare

                File mechDirectory = new File(System.getProperty("user.home") + "\\Downloads\\mech\\"); //check if file exists
                boolean tempExists = mechDirectory.exists(); //linear sarch to see if prefix exists

                double localTarget = 0;
                System.out.println("latest ver " + verDouble);

                if(tempExists) {
                    File mechLocal = new File(System.getProperty("user.home") + "\\Downloads\\mech\\");
                    File[] listOfFiles = mechLocal.listFiles();

                    for (int i = 0; i < listOfFiles.length; i++) {
                        if (listOfFiles[i].isFile()) {
                            listOfFiles[i].getName();
                        } else if (listOfFiles[i].isDirectory()) {
                            listOfFiles[i].getName();
                        }
                    }
                    String[] localVerNum = listOfFiles[0].getName().split("v0.");
                    String[] tempTarget = localVerNum[1].split(".exe");
                    localTarget = Double.parseDouble(tempTarget[0]);
                    System.out.println("local ver " + localTarget);

                    if(verDouble > localTarget) { //TODO: replace 0.4 with read local version
                        String mechPath = System.getProperty("user.home") + "\\Downloads\\mech";
                        File mechFolder = new File(mechPath); //assign var to mech folder location
                        System.out.println("updating");

                        install(); //add check for successful install
                        delete(mechFolder); //deletes old mech folder i think
                        unpack(System.getProperty("user.home") + "\\Downloads\\mech.zip", System.getProperty("user.home") + "\\Downloads\\mech");
                        build(verDouble);
                        System.out.println("success");
                    } else {
                        System.out.println("You are up to date :^)");
                    }
                } else { //first time set up
                    String mechPath = System.getProperty("user.home") + "\\Downloads\\mech";
                    System.out.println("installing first time");

                    install(); //add check for successful install
                    unpack(System.getProperty("user.home") + "\\Downloads\\mech.zip", System.getProperty("user.home") + "\\Downloads\\mech");
                    build(verDouble);
                    System.out.println("success");
                }
                   
        }
    }
}

//TODO: GUI lets user specify install location
//TODO: changes for lek version
//      change install location (need to move folders into steam ones)
//      change github sided version detector to whatever tag system EAP will use
//      get EAP to make releases in 7zip
