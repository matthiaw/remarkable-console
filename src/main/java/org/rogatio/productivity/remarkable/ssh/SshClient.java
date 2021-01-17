/*
 * Remarkable Console - Copyright (C) 2021 Matthias Wegner
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.rogatio.productivity.remarkable.ssh;

import java.io.File;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * The Class SshClient provides a simple ssh-client to connect to the remarkable
 * in the same local wireless network
 * 
 * @author Matthias Wegner
 */
public class SshClient {

	/** The Constant logger. */
	private static final Logger logger = LogManager.getLogger(SshClient.class);

	/** The session of the ssh-connection */
	private Session session = null;

	/**
	 * Disconnect the ssh-session
	 */
	public void disconnect() {
		if (session != null) {
			session.disconnect();
		}
	}

	/**
	 * Connect client to remarkable
	 *
	 * @param username the username
	 * @param password the password
	 * @param host     the host
	 * @param port     the port
	 */
	private void connect(String username, String password, String host, int port) {
		try {
			session = new JSch().getSession(username, host, port);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setTimeout(3000);
			session.connect();
		} catch (JSchException e) {
			// logger.error("Error creating SSH session", e);
		}

	}

	/**
	 * Checks if client is connected.
	 *
	 * @return true, if is connected
	 */
	public boolean isConnected() {
		if (session != null) {
			if (session.isConnected()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Instantiates a new ssh client.
	 *
	 * @param username the username
	 * @param password the password
	 * @param host     the host
	 * @param port     the port
	 */
	public SshClient(String username, String password, String host, int port) {
		connect(username, password, host, port);
	}

	/**
	 * Open SFTP channel.
	 *
	 * @return the channel sftp
	 */
	private ChannelSftp openSFTPchannel() {
		if (this.isConnected()) {
			try {
				Channel channel = session.openChannel("sftp");
				channel.connect();
				return (ChannelSftp) channel;
			} catch (JSchException e) {
				logger.error("Error creating SFTP channel", e);
			}
		}
		return null;
	}

	/**
	 * Close SFTP channel.
	 *
	 * @param channelSftp the channel sftp
	 */
	private void closeSFTPchannel(ChannelSftp channelSftp) {
		if (channelSftp != null)
			channelSftp.disconnect();
	}

	/**
	 * Upload file from local to remote connected remarkable
	 *
	 * @param remoteFile the remote file
	 * @param localFile  the local file
	 */
	public void uploadFile(String remoteFile, String localFile) {
		if (this.isConnected()) {
			ChannelSftp channelSftp = openSFTPchannel();
			try {
				channelSftp.put(localFile, remoteFile);
			} catch (SftpException e) {
				logger.error("Error uploading file", e);
			}
			closeSFTPchannel(channelSftp);
		}
	}

	/**
	 * Download file from remote remarkable to local client. Instantiates new SFTP
	 * channel.
	 *
	 * @param remoteFile the remote file
	 * @param localFile  the local file
	 */
	public void downloadFile(String remoteFile, String localFile) {
		if (this.isConnected()) {
			ChannelSftp channelSftp = openSFTPchannel();
			try {
				channelSftp.get(remoteFile, localFile);
			} catch (SftpException e) {
				logger.error("Error downloading file", e);
			}
			closeSFTPchannel(channelSftp);
		}
	}

	/**
	 * Download file from remote remarkable to local client
	 *
	 * @param remoteFile  the remote file
	 * @param localFile   the local file
	 * @param channelSftp the channel sftp
	 */
	public void downloadFile(String remoteFile, String localFile, ChannelSftp channelSftp) {
		if (this.isConnected()) {
			try {
				channelSftp.get(remoteFile, localFile);
			} catch (SftpException e) {
				logger.error("Error downloading file" + e.getCause().toString(), e);
			}
		}
	}

	/**
	 * Download directory without subfolders
	 *
	 * @param sourcePath the source path
	 * @param destPath   the destination path
	 */
	public void downloadDir(String sourcePath, String destPath) { // With subfolders and all files.
		if (this.isConnected()) {
			ChannelSftp channelSftp = openSFTPchannel();
			try {
				// Create local folders if absent.
				try {
					new File(destPath).mkdirs();
				} catch (Exception e) {
					System.out.println("Error at : " + destPath);
				}

				String aPath = new File(destPath).getAbsolutePath();
				channelSftp.lcd(aPath);

				@SuppressWarnings("unchecked")
				Vector<LsEntry> list = channelSftp.ls(sourcePath);
				for (LsEntry oListItem : list) {
					String filename = oListItem.getFilename();
					if (!oListItem.getAttrs().isDir()) {
						downloadFile(sourcePath + filename, filename, channelSftp);
						logger.debug("Copy: " + sourcePath + filename + " --> " + destPath + "/" + filename);
					}
				}

			} catch (SftpException e) {
				logger.error("Error downloading dir" + e.getCause().toString(), e);
			}
			closeSFTPchannel(channelSftp);
		}
	}

	/**
	 * List directory.
	 *
	 * @param directory the directory
	 * @return the vector
	 */
	public Vector<LsEntry> listDirectory(String directory) {
		if (this.isConnected()) {
			ChannelSftp channelSftp = openSFTPchannel();
			try {
				@SuppressWarnings("unchecked")
				Vector<LsEntry> filelist = channelSftp.ls(directory);
				return filelist;
			} catch (SftpException e) {
				logger.error("Error uploading file", e);
			}
			closeSFTPchannel(channelSftp);
		}
		return null;
	}

}
