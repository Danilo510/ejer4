package archivo;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class servidor {
    private static final int puerto = 5555;
    private Socket client;
    private static final File raiz = new File("/home/somaalva/Escritorio/SERVIDOR"); //Directorio con archivos
    private DataInputStream IN;
    private DataOutputStream OUT;

    servidor() throws IOException{
        ServerSocket server = new ServerSocket(puerto);
        System.out.println("server online...");
        if(!raiz.exists()){
            raiz.mkdir();
        }

        //Recibir peticiones siempre
        while (true){
            //Conexion entrante
            client = server.accept();
            IN = new DataInputStream(client.getInputStream());
            OUT= new DataOutputStream(client.getOutputStream());

            System.out.println("========================");
            System.out.println("conexion desde "+client);
            try {
                //MAntener comunicacion con cliente
                while (true){
                    String Mensaje = IN.readUTF();
                    switch (Mensaje){
                        //Solicitud de archivo
                        case "1":
                            Mensaje1();
                            break;
                        //Listar Archivos
                        case "2":
                            Mensaje2();
                            break;
                    }
                }
            }catch (Exception e){
                System.out.println("Cliente desconectado \n");
                client.close();
            }
        }
    }

    private void Mensaje1()throws IOException{
        //Nombre de archivo solicitado
        String archivito = IN.readUTF();
        File archi = new File(raiz.getPath()+"//"+archivito);

        //Envio mensaje de existencia
        OUT.writeBoolean(archi.exists());
        if(archi.exists()) {
            BufferedInputStream BIS = new BufferedInputStream(new FileInputStream(archi));
            BufferedOutputStream BOS = new BufferedOutputStream(client.getOutputStream());
            
            //Obtenemos el tama;o del arcchivo para un buffer dinamico
             //long size = archi.length();
             //int nuevo_size = (int)size;
             //System.out.println( "tamaño del buffer: "+ nuevo_size);
            // Envio del archivo
            byte[] buffer = new byte[8192];
            int in;
            while ((in = BIS.read(buffer)) != -1) {
                BOS.write(buffer, 0, in);
            }
            System.out.println("Envio: "+ archi.getName());
            BIS.close();
            BOS.close();
        }else{
            System.out.println("NO existe: "+archivito);
        }
    }

    private void Mensaje2() throws IOException{
        String archivos = Arrays.toString((raiz.list()));
        System.out.println("Enviando lista de archivos");
        OUT.writeUTF( archivos.substring(1, archivos.length()-1) );

        File tam [] = raiz.listFiles();
        long arr [] =  new long[tam.length];
        for (int i = 0; i < tam.length ; i++) {
            arr[i] = tam[i].length();
        }

        String tamanos = Arrays.toString(arr);
        OUT.writeUTF( tamanos.substring(1, tamanos.length()-1) );
    }

    public static void main(String[] args) throws IOException {
        new servidor();
    }
}