package com.austinramsay.timekeeperserver;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManager {





    /*
    // Declare final path values used for necessary stored server files
     */
    private static final String SERVER_DIR = System.getProperty("user.home");
    private static final File ORGANIZATION_MANAGER_FILE = new File(SERVER_DIR + "/.orgs.timekeeper");





    /**
     Find and load server files in main Time Keeper directory.
     The default path is the user's home directory.
     */
    public static boolean loadOrganizationManager()
    {
        /*
        // Check if the server home has an organization file
        */
        System.out.printf("Checking %s for existing server files...%n", SERVER_DIR);


        // Check if 'Organization Manager' file exists to set the static 'org_manager' variable
        StringBuilder organizationsExist = new StringBuilder();
        organizationsExist.append("Organitzation manager file: ");
        if (FileManager.ORGANIZATION_MANAGER_FILE.exists())
        {
            organizationsExist.append("Found!");
        }
        else
        {
            organizationsExist.append("Not found.");

            if (createOrganizationManagerFile(FileManager.ORGANIZATION_MANAGER_FILE))
                organizationsExist.append("\nNew organizations file created.");
        }


        // Output current status
        System.out.printf("%s%n", organizationsExist);


        // Attempt to read the report log file and place into the static report log variable
        StringBuilder organizationLoadStatus = new StringBuilder();
        ObjectInputStream ois = null;
        try {

            // Open a file stream to the default path of the Organization Manager file
            ois = new ObjectInputStream(new FileInputStream(FileManager.ORGANIZATION_MANAGER_FILE));

            // Set the Organization Manager to our static variable
            TimeKeeperServer.org_manager = (OrganizationManager)ois.readObject();

            // Log status and return true - Success
            organizationLoadStatus.append("Organitzation manager ready.");
            return true;

        } catch (InvalidClassException e) {

            organizationLoadStatus.append("\nAttempted to load organization manager file, but the version is not current.");
            e.printStackTrace();

        } catch (FileNotFoundException e) {

            organizationLoadStatus.append("\nAttempted to load organitzation manager file, but file was not found.");
            e.printStackTrace();

        } catch (ClassNotFoundException e) {

            organizationLoadStatus.append("\nAttempted to load organitzation manager file, but failed to cast the object.");
            e.printStackTrace();

        } catch (IOException e) {

            organizationLoadStatus.append("\nAttempted to load organitzation manager file. IOException occured.");
            e.printStackTrace();

        } finally {
            // Output current status
            System.out.printf("%s%n", organizationLoadStatus);

            try {
                if (ois != null)
                    ois.close();
            } catch (IOException e) {  System.out.println("Failed to close resources.");  e.printStackTrace();  }
        }


        // Failed to get the Organization Manager
        return false;
    }





    /**
     * @param newFile the file to be created
     * @return
     */
    public static boolean createOrganizationManagerFile(File newFile)
    {
        ObjectOutputStream oos = null;

        try
        {
            newFile.createNewFile();
            OrganizationManager new_org_manager = new OrganizationManager();
            oos = new ObjectOutputStream(new FileOutputStream(newFile));
            oos.writeObject(new_org_manager);
            return true;
        }
        catch (IOException e)
        {
            System.out.printf("Failed to create new file: %s%n", newFile.getName());
            e.printStackTrace();
            return false;
        }
        finally
        {
            try {
                if (oos != null)
                    oos.close();
            } catch (IOException e) {  System.out.println("Failed to close resources.");  e.printStackTrace();  }
        }
    }





    /**
     Saves the Organization Manager to the server's local file.
     Path is equal to the static final string ORGANIZATION_MANAGER_FILE.
     */
    public static boolean updateOrganizationManager()
    {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        ObjectOutputStream oos = null;

        try
        {
            oos = new ObjectOutputStream(new FileOutputStream(FileManager.ORGANIZATION_MANAGER_FILE));
            oos.writeObject(TimeKeeperServer.org_manager);
            System.out.println(dateFormat.format(new Date()) + ": Organization manager saved.");

            return true;
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Failed to save files. File not found.");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            System.out.println("Failed to save files. IOException occured.");
            e.printStackTrace();
        }
        finally
        {
            try {
                if (oos != null)
                    oos.close();
            } catch(IOException e) {  System.out.println("Failed to close resources.");  e.printStackTrace();  }
        }

        return false;
    }
}
