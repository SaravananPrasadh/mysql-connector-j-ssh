package com.mysql.cj.util;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHonJSch extends Util {

    public int getSSHTunnel(String sessionhost, int sessionPort, String sessionUser, String sessionPassword, String sessionKeyFile, String authType,
            String sessionKnownHosts, String finalsshHost, int finalsshPort) throws Exception, JSchException {

        // Create a new JSch object to intialize the session
        JSch JSchsession = new JSch();
        // Create a new session for the SSH connection
        Session LocalToSSHSession = JSchsession.getSession(sessionUser, sessionhost, sessionPort);
        switch (authType) {
            case "KEY":
                // Need to use the Private Key for the SSH connection
                if (sessionKeyFile != null && sessionPassword != null) {
                    JSchsession.addIdentity(sessionKeyFile, sessionPassword);
                } else if (sessionKeyFile != null) {
                    JSchsession.addIdentity(sessionKeyFile);
                }
                break;
            case "USRPASS":
                // Need to use the Username and Password for the SSH connection
                if (sessionUser != null && sessionPassword != null) {
                    LocalToSSHSession.setPassword(sessionPassword);
                }
                break;
        }
        // set the knownhosts file if the file is defined.
        if (sessionKnownHosts != null) {
            JSchsession.setKnownHosts(sessionKnownHosts);
        } else {
            LocalToSSHSession.setConfig("StrictHostKeyChecking", "no");
        }
        // connect to the bastion host
        LocalToSSHSession.connect();
        // forward the local available port to the final ssh remote port
        int assigned_port = LocalToSSHSession.setPortForwardingL(0, finalsshHost, finalsshPort);
        return assigned_port;
    }
}
