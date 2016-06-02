package me.stuntguy3000.java.telegames.handler;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import me.stuntguy3000.java.telegames.Telegames;

/**
 * Automatically update this plugin
 *
 * @author bo0tzz
 * @author stuntguy3000
 */
public class UpdateHandler implements Runnable {

    private final String fileName;
    private final Telegames instance;
    private final String projectName;

    public UpdateHandler(Telegames instance, String projectName, String fileName) {
        this.instance = instance;
        this.projectName = projectName;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        File build = new File("build");
        File jar = new File(fileName + ".new");

        LogHandler.debug(String.valueOf(build));

        int currentBuild = instance.getCurrentBuild();
        int newBuild;

        while (true) {
            try {
                HttpResponse<String> response = Unirest.get("http://ci.zackpollard.pro/job/" + projectName + "/lastSuccessfulBuild/buildNumber").asString();
                
                if (response.getStatus() == 200) {
                    newBuild = Integer.parseInt(response.getBody());
                    LogHandler.debug(String.valueOf(newBuild));
                } else {
                    LogHandler.log("[ERROR] Updater status code: " + response.getStatus());
                    instance.sendToAdmins("[ERROR] Updater status code: " + response.getStatus() + "\n\nUpdater stopped.");
                    return;
                }
            } catch (UnirestException e) {
                e.printStackTrace();
                return;
            }

            if (newBuild > currentBuild) {
                LogHandler.log("Downloading build #" + newBuild);
                instance.sendToAdmins("Downloading build #" + newBuild);
                try {
                    FileUtils.writeStringToFile(build, String.valueOf(newBuild));
                    FileUtils.copyURLToFile(new URL("http://ci.zackpollard.pro/job/" + projectName + "/lastSuccessfulBuild/artifact/target/" + fileName + ".jar"), jar);
                    LogHandler.log("Build #" + newBuild + " downloaded. Restarting...");
                    instance.getConfigHandler().saveConfig("stats.json");
                    if (!instance.isDevelopmentMode()) {
                        Telegames.getInstance().getUpdaterAnnouncerHandler().runUpdater();
                    }
                    instance.sendToAdmins("Build #" + newBuild + " downloaded. Restarting...");
                } catch (IOException e) {
                    instance.sendToAdmins("Updater failed!");
                    e.printStackTrace();
                    return;
                }

                System.exit(0);
            }
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}