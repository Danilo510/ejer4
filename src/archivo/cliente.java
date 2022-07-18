package archivo;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class cliente {
    private static final String IP  = "localhost";
    private static final int PUERTO = 5555;
    private Socket connect;
    private DataInputStream IN;
    private DataOutputStream OUT;
    private static File Directorio = new File("\\home\\somaalva\\Descargas"); //Almacenar archivos entrantes

    private static final StringBuilder sb = new StringBuilder();

    cliente() throws IOException{
        if(!Directorio.exists()){
            Directorio.mkdir();
        }
        //Conexion
        connect = new Socket(IP, PUERTO);
        IN = new DataInputStream(connect.getInputStream());  //flujo entrada S -> C
        OUT = new DataOutputStream(connect.getOutputStream()); //flujo salida C -> S

         verArchivos();
         pedirArchivos();  
    }
    private void verArchivos() throws IOException{
        OUT.writeUTF("2");
        String lista = IN.readUTF();
        String tamanos = IN.readUTF();
        if (lista.isEmpty()){
            return;
        }
        int num = 0;

        String tam [] = (tamanos).split(",");
        System.out.println("\t===================================");
        System.out.println("\tARCHIVOS DISPONIBLES EN EL SERVIDOR");
        System.out.println("\t===================================");
        for(String archivo : (" "+lista).split(",")){
            System.out.println("\t"+ (++num) +archivo +"  "+ Long.parseLong(tam[num-1].trim())+ " Bytes");
        }
    }

    public void pedirArchivos() throws IOException{
        OUT.writeUTF("1");
        System.out.println("Que archivo desea descargar:");
        String archivo = new Scanner(System.in).nextLine();
        OUT.writeUTF(archivo);
        if(IN.readBoolean()){
            byte [] buffer = new byte[1024];
            BufferedInputStream BIS = new BufferedInputStream(connect.getInputStream());
            BufferedOutputStream BOS = new BufferedOutputStream(new FileOutputStream(Directorio.getName()+"/"+archivo));
            int in;
            long contador = 0;
            while ((in = BIS.read(buffer)) != -1){
                BOS.write(buffer,0,in);
            }
            System.out.println("Archivo recibido: "+archivo);
            BOS.close();
        }else{
            System.out.println("Archivo no existe");
        }
    }

    public static void main(String[] args)throws IOException {
        new cliente();
    }
}
