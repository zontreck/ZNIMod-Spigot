package zeenai.server.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import com.google.gson.Gson;

public class WebToken implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        p.sendMessage(ChatColor.RED + "[Zontreck.dev] " + ChatColor.GREEN + "Generating access token");

        token playerToken = new token();
        playerToken.SetKey(p.getName());

        p.sendMessage(ChatColor.RED + "[Zontreck.dev] " + ChatColor.WHITE + "Your personal access token is "
                + ChatColor.DARK_PURPLE + playerToken.GetKey());

        // Send this to the server
        File xFile = new File("C:\\xampp\\mc.zontreck.dev\\confs\\" + playerToken.GetKey() + ".var");
        PrintWriter pw;
        try {
            class OutputFormat {
                public String username;
                public String token;
                public long reserve;
                public OutputFormat(){
                    username="";
                    token="";
                    reserve=0L;
                }

                @Override
                public String toString(){
                    return "User: "+username+"; Token: "+token+"; ReservedAt: "+reserve;
                }

                public String toJson(){
                    return "{\"username\":\""+username+"\", \"token\":\""+token+"\", \"reserve\":"+reserve+"}";
                }
            }
            pw = new PrintWriter(xFile);
            OutputFormat of = new OutputFormat();
            of.username = p.getName();
            of.token = playerToken.GetKey();
            of.reserve = Instant.now().getEpochSecond();
            pw.write(of.toJson());
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        
        
        return true;
    }

    public class token {
        public token(){
            NewKey();
        }
        private String _key;

        public String GetKey() {
            return _key;
        }

        public void SetKey(String newValue) {
            if (newValue != "")
                CalcKey(newValue);
            else
                NewKey();
        }

        public void SetRawKey(String Key) {
            _key = Key;
        }

        public void CalcKey(String val) {
            char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
            String valid = "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            while (valid.length() < val.length()) {
                valid += valid;
            }
            

            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                for (int j = 0; j < val.length(); j++) {
                    byte[] MDData = md.digest((_key+val+Instant.now().toEpochMilli()).getBytes());
                    char[] hexChars = new char[MDData.length * 2];
                    for (int x =0;x < MDData.length; x++) {
                        byte b = MDData[x];
                        int v = b & 0xFF;
                        hexChars[x * 2] = HEX_ARRAY[v >>> 4];
                        hexChars[x * 2 + 1] = HEX_ARRAY[v & 0x0F];
                    }
                    String result = new String(hexChars);
                    _key = result.substring(0,5);
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        public void NewKey(){
            _key="";
            for (int i = 0; i < 6; i++) {
                _key += "0";
            }
        }
    }
}
