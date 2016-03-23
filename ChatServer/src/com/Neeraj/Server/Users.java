package com.Neeraj.Server;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class Users {
	
	private Statement statement;
	
	private Connection con;
	
	private String url;

	
	public Users() {
		try{
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		url = "jdbc:sqlserver://127.0.0.1:59276;databaseName=SecureChatDB;integratedSecurity=true;";
        con = DriverManager.getConnection(url);
        statement = con.createStatement();
   		}catch (Exception e)
        {
            e.printStackTrace();
        }
	}
	public void addUser(String name, String passwd) throws SQLException, NoSuchAlgorithmException {
		String salt = getSalt();
        String securePassword = getSecurePassword(passwd, salt);
    	statement.execute("exec addUser '"+name+"' ,'"+securePassword+"', '"+salt+"'");

	}
	public boolean checkUser(String userName) throws SQLException{
		ResultSet result = statement.executeQuery("exec checkuser '"+ userName + "'");
		return result.next();
		
	}
	public boolean checkUsersID(String userName,String userPasswd) throws SQLException{
		ResultSet result = statement.executeQuery("exec FindSalt '"+ userName + "'");
		String salt;
		String hashedPasswd;
		if(result.next()){
			salt= result.getString(1);
			hashedPasswd = getSecurePassword(userPasswd, salt);
			ResultSet result2 = statement.executeQuery("exec checkPassword '"+ userName + "','"+hashedPasswd+"'");
			return result2.next();
		}
		return false;
	}
	private static String getSalt() throws NoSuchAlgorithmException
	{
	    //Always use a SecureRandom generator
	    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
	    //Create array for salt
	    byte[] salt = new byte[16];
	    //Get a random salt
	    sr.nextBytes(salt);
	    //return salt
	    return salt.toString();
	}
	private static String getSecurePassword(String passwordToHash, String salt)
    {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Update the md with salt Bytes
            md.update(salt.getBytes());
            //Get the hash's bytes 
            byte[] bytes = md.digest(passwordToHash.getBytes());
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        } 
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
	
}
