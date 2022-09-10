package com.krazzzzymonkey.catalyst.managers.accountManager;


import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.managers.accountManager.alt.AccountData;
import com.krazzzzymonkey.catalyst.managers.accountManager.alt.AltDatabase;
import com.krazzzzymonkey.catalyst.managers.accountManager.tools.EncryptionTools;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;

import static com.krazzzzymonkey.catalyst.managers.FileManager.ALT_DIR;

/**
 * @author The_Fireplace
 */
public final class Standards {

	public static final String cfgn = "catalyst alts.cfg";
	public static final String pwdn = "catalyst alts.encrypted";

	public static String getPassword(){
		File passwordFile = new File(ALT_DIR, pwdn);
		if(passwordFile.exists()){
			String pass;
			try {
				ObjectInputStream stream = new ObjectInputStream(new FileInputStream(passwordFile));
				pass = (String) stream.readObject();
				stream.close();
			} catch (IOException | ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
			return pass;
		}else{
			String newPass = EncryptionTools.generatePassword();
			try {
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(passwordFile));
				out.writeObject(newPass);
				out.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			try{
				Path file = passwordFile.toPath();
				DosFileAttributes attr = Files.readAttributes(file, DosFileAttributes.class);
				DosFileAttributeView view = Files.getFileAttributeView(file, DosFileAttributeView.class);
				if(!attr.isHidden())
					view.setHidden(true);
			}catch(Exception e){
				e.printStackTrace();
			}
			return newPass;
		}
	}

	public static void updateFolder(){
		String dir;
		String OS = (System.getProperty("os.name")).toUpperCase();
		if(OS.contains("WIN")){
			dir=System.getenv("AppData");
		}else{
			dir=System.getProperty("user.home");
			if(OS.contains("MAC"))
				dir+="/Library/Application Support";
		}

	}

	public static void importAccounts(){
		processData(getConfigV3());
		processData(getConfigV2());
		processData(getConfigV1(), false);
	}

	private static boolean hasData(AccountData data){
		for(AccountData edata:AltDatabase.getInstance().getAlts()){
			if(edata.equalsBasic(data)){
				return true;
			}
		}
		return false;
	}

	private static void processData(Config olddata){
		processData(olddata, true);
	}

	private static void processData(Config olddata, boolean decrypt){
		if(olddata != null){
			for(AccountData data:((AltDatabase) olddata.getKey("altaccounts")).getAlts()){
				AccountData data2 = convertData(data, decrypt);
				if(!hasData(data2))
					AltDatabase.getInstance().getAlts().add(data2);
			}
		}
	}

	private static ExtendedAccountData convertData(AccountData oldData, boolean decrypt){
		if(decrypt){
			if(oldData instanceof ExtendedAccountData)
				return new ExtendedAccountData(EncryptionTools.decodeOld(oldData.user), EncryptionTools.decodeOld(oldData.pass), oldData.alias, ((ExtendedAccountData) oldData).useCount, ((ExtendedAccountData) oldData).lastused, ((ExtendedAccountData) oldData).premium);
			else
				return new ExtendedAccountData(EncryptionTools.decodeOld(oldData.user), EncryptionTools.decodeOld(oldData.pass), oldData.alias);
		}else{
			if(oldData instanceof ExtendedAccountData)
				return new ExtendedAccountData(oldData.user, oldData.pass, oldData.alias, ((ExtendedAccountData) oldData).useCount, ((ExtendedAccountData) oldData).lastused, ((ExtendedAccountData) oldData).premium);
			else
				return new ExtendedAccountData(oldData.user, oldData.pass, oldData.alias);
		}
	}

	private static Config getConfigV3() {
		File f = new File(ALT_DIR, ".ias");
		Config cfg = null;

			try {
				ObjectInputStream stream = new ObjectInputStream(new FileInputStream(f));
				cfg = (Config) stream.readObject();
				stream.close();
			} catch (IOException | ClassNotFoundException e) {
				//empty catch block
			}
			f.delete();

		return cfg;
	}

	private static Config getConfigV2() {
		File f = new File(ALT_DIR, ".ias");
		Config cfg = null;
		if (f.exists()) {
			Main.logger.info(f.getName() + "Exists");
			try {
				ObjectInputStream stream = new ObjectInputStream(new FileInputStream(f));
				cfg = (Config) stream.readObject();
				stream.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			f.delete();
		}
		return cfg;
	}

	private static Config getConfigV1(){
		File f = new File(ALT_DIR, "user.cfg");
		Config cfg = null;
		if (f.exists()) {
			try {
				ObjectInputStream stream = new ObjectInputStream(new FileInputStream(f));
				cfg = (Config) stream.readObject();
				stream.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			f.delete();
		}
		return cfg;
	}
}
