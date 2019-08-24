import java.io.*;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

import mpi.MPI;

public class Main {

    static int qtdThread;
    static int nrThread;

    private static void lerArquivos(ArrayList<Centroide> centroides, ArrayList<Elemento> elementos, String nomeArquivoCentroide, String nomeArquivoBase) {
        ArrayList<Integer> aux1 = new ArrayList<>();
        ArrayList<Integer> aux2 = new ArrayList<>();
        String linha;
        try {
            FileReader arq = new FileReader(nomeArquivoCentroide);
            BufferedReader lerArq = new BufferedReader(arq);
            while ((linha = lerArq.readLine()) != null) {
                String[] lido = linha.split(",");
                for(int i = 0; i < lido.length; i++){
                    aux1.add(parseInt(lido[i]));
                }
                centroides.add( new Centroide(aux1));
                aux1.clear();
            }
            arq.close();
        } catch(IOException e) {
            System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
        }
        try {
            FileReader arq = new FileReader(nomeArquivoBase);
            BufferedReader lerArq = new BufferedReader(arq);
            while((linha = lerArq.readLine()) != null) {
                String[] lido = linha.split(",");
                for(int i = 0; i < lido.length; i++) {
                    aux2.add(parseInt(lido[i]));
                }
                elementos.add(new Elemento(aux2));
                aux2.clear();
            }
            arq.close();
        } catch(IOException e) {
            System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
        }
    }

    private static void escreveSaida(ArrayList<Elemento> elementos, long tempo, int iteracoes , int modo, int qtdThreads) {
        try {
            int cont = 0;
            FileWriter arq = new FileWriter("saida.txt");
            BufferedWriter escreveArq = new BufferedWriter(arq);
            escreveArq.append("Base: " + elementos.get(0).getCoordenadas().size() + "\n");
            escreveArq.append("Execução: " + (modo == 1 ? "Sequencial" : "Paralelo \nQuantidade Threads: " + qtdThreads) + "\n");
            escreveArq.append("Iterações: " + iteracoes + "\n");
            escreveArq.append("Tempo de Execução: " + tempo + " ms\n");
            for (Elemento e : elementos) {
                escreveArq.append("id=" + cont + ", classe=" + e.getIndiceCentroide() + "\n");
                escreveArq.flush();
                cont++;
            }
            arq.close();
        } catch (IOException e) {
            System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
        }
    }

    public static void main(String[] args) throws InterruptedException{
        MPI.Init(args);
        qtdThread = MPI.COMM_WORLD.Size();
        nrThread = MPI.COMM_WORLD.Rank();
        String nomeArquivoCentroide, nomeArquivoBase;
        switch (Integer.parseInt(args[3])){
            case 2:
                nomeArquivoBase = "bases/int_base_161.data";
                nomeArquivoCentroide = "bases/int_centroid_161_20.data";
                break;
            case 3:
                nomeArquivoBase = "bases/int_base_256.data";
                nomeArquivoCentroide = "bases/int_centroid_256_20.data";
                break;
            case 4:
                nomeArquivoBase = "bases/int_base_1380.data";
                nomeArquivoCentroide = "bases/int_centroid_1380_20.data";
                break;
            case 5:
                nomeArquivoBase = "bases/int_base_1601.data";
                nomeArquivoCentroide = "bases/int_centroid_1601_20.data";
                break;
            default:
                nomeArquivoBase = "bases/int_base_59.data";
                nomeArquivoCentroide = "bases/int_centroid_59_20.data";
                break;
        }

        ArrayList<Elemento> elementos = new ArrayList<>();
        ArrayList<Centroide> centroides = new ArrayList<>();
        lerArquivos(centroides, elementos, nomeArquivoCentroide, nomeArquivoBase);
        long tempoInicial, tempoFinal, tempo;

        if(qtdThread == 1) {
            tempoInicial = System.currentTimeMillis();
            KMeansSequencial kmeans = new KMeansSequencial(centroides,elementos);
            kmeans.executa();
            tempoFinal = System.currentTimeMillis();
            tempo = tempoFinal - tempoInicial;
            escreveSaida(elementos, tempo, kmeans.getIteracoes(), 1, qtdThread);
        } else {
            tempoInicial = System.currentTimeMillis();
            KMeansMPI kmeans = new KMeansMPI(centroides, elementos, qtdThread);
            kmeans.executa();
            tempoFinal = System.currentTimeMillis();
            tempo = tempoFinal - tempoInicial;
            escreveSaida(elementos, tempo, kmeans.getIteracoes(), 2, qtdThread);
        }

        MPI.Finalize();
    }
}
