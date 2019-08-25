import mpi.MPI;

import java.util.ArrayList;

public class KMeansMPI {
    private ArrayList<Centroide> centroides;
    private ArrayList<Elemento> elementos;
    private int iteracoes = 0;
    private int quantidadeThreads;

    public int getIteracoes() {
        return iteracoes;
    }

    public void incrementaIteracaoes() {
        this.iteracoes++;
    }

    public KMeansMPI(ArrayList<Centroide> centroides, ArrayList<Elemento> elementos, int quantidadeThreads) {
        this.centroides = (ArrayList) centroides.clone();
        this.elementos = (ArrayList) elementos.clone();
        this.quantidadeThreads = quantidadeThreads;
    }

    public int calculaDistancia(Elemento e, Centroide c) {
        int qtd = e.getCoordenadas().size();
        int somatorio = 0;
        for(int i = 0; i < qtd; i++) {
            somatorio += Math.pow(e.getCoordenadas().get(i) - c.getCoordenadas().get(i), 2.0);
        }
        return (int) Math.sqrt(somatorio);
    }

    public int melhorCentroide (Elemento e) {
        Integer menorDistancia = Integer.MAX_VALUE;
        Centroide melhor = new Centroide();
        for(Centroide c : this.centroides) {
            int aux = calculaDistancia(e, c);
            if(menorDistancia > aux) {
                menorDistancia = aux;
                melhor = c;
            }
        }
        return this.centroides.indexOf(melhor);
    }

    public void executa() {
        Boolean moveu;
        int qtdElementos = this.elementos.size();
        int qtdElementosThread = qtdElementos / (this.quantidadeThreads - 1);
        int inicio = qtdElementosThread * (Main.nrThread - 1);
        int fim;

        if(Main.nrThread == (this.quantidadeThreads - 1)) {
            fim = inicio + (qtdElementosThread - 1) + qtdElementos % (this.quantidadeThreads - 1);
        } else {
            fim = inicio + qtdElementosThread - 1;
        }

        do{
            incrementaIteracaoes();

            for(Centroide c : this.centroides) {
                c.getElementos().clear();
            }
            moveu = false;

            int [][] centroidesMatriz = new int[20][this.centroides.get(0).getCoordenadas().size()];

            if(Main.nrThread == 0) {
                Integer [][] testeCentroide = new Integer[20][this.centroides.get(0).getCoordenadas().size()];

                for(int i = 0; i < 20; i++) {
                    testeCentroide[i] = this.centroides.get(i).getCoordenadas().toArray(testeCentroide[i]);
                }

                int [][] testeAgoravai = new int[20][this.centroides.get(0).getCoordenadas().size()];

                for(int i = 0; i < 20; i++) {
                    for(int j = 0; j < this.centroides.get(0).getCoordenadas().size(); j++) {
                        testeAgoravai[i][j] = testeCentroide[i][j];
                    }
                }

                for(int i = 1; i < Main.qtdThread; i++) {
                    for(int j = 0; j < 20; j++) {
                        MPI.COMM_WORLD.Send(testeAgoravai[j], 0, this.centroides.get(0).getCoordenadas().size(), MPI.INT, i, 0);
                    }
                }
            } else {
                for(int i = 0; i < 20; i++) {
                    MPI.COMM_WORLD.Recv(centroidesMatriz[i], 0, this.centroides.get(0).getCoordenadas().size(), MPI.INT, 0, 0);
                }
            }

            MPI.COMM_WORLD.Barrier();

            if(Main.nrThread != 0)  {
                for(int i = 0; i < 20; i ++) {
                    ArrayList<Integer> aux = new ArrayList<>();
                    for(int j = 0; j < centroidesMatriz[i].length; j++) {
                        aux.add(centroidesMatriz[i][j]);
                    }
                    this.centroides.get(i).setCoordenadas(aux);
                }
                for(int i = inicio; i <= fim; i++) {
                    int [] aux = new int[1];
                    aux[0] = i;
                    MPI.COMM_WORLD.Send(aux, 0, 1, MPI.INT, 0, 0);
                    aux[0] = melhorCentroide(this.elementos.get(i));
                    MPI.COMM_WORLD.Send(aux, 0, 1, MPI.INT, 0, 1);
                }
            } else {
                int [] aux  = new int[1];
                int numeroElemento, melhorCentroide;
                for(int i = 1; i < quantidadeThreads; i++) {
                    if(i != quantidadeThreads - 1) {
                        for(int j = 0; j < qtdElementosThread; j++) {
                            MPI.COMM_WORLD.Recv(aux, 0, 1, MPI.INT, i, 0);
                            numeroElemento = aux[0];
                            MPI.COMM_WORLD.Recv(aux, 0, 1, MPI.INT, i, 1);
                            melhorCentroide = aux[0];
                            this.centroides.get(melhorCentroide).addElemento(this.elementos.get(numeroElemento));
                        }
                    } else {
                        for(int j = 0; j < qtdElementosThread + (qtdElementos % (this.quantidadeThreads - 1)); j++) {
                            MPI.COMM_WORLD.Recv(aux, 0, 1, MPI.INT, i, 0);
                            numeroElemento = aux[0];
                            MPI.COMM_WORLD.Recv(aux, 0, 1, MPI.INT, i, 1);
                            melhorCentroide = aux[0];
                            this.centroides.get(melhorCentroide).addElemento(this.elementos.get(numeroElemento));
                        }
                    }
                }
            }

            MPI.COMM_WORLD.Barrier();

            int [] seMoveu = new int[1];
            if(Main.nrThread == 0) {
                for (Centroide c : this.centroides) {
                    if (c.moveCentroide()) {
                        moveu = true;
                    }
                }

                seMoveu[0] = moveu ? 1 : 0;
            }

            MPI.COMM_WORLD.Bcast(seMoveu, 0, 1, MPI.INT, 0);

            moveu = seMoveu[0] == 1;

            MPI.COMM_WORLD.Barrier();

        } while(moveu);

        if(Main.nrThread == 0) {
            int soma = 0;
            for(int i = 0; i < 20; i++) {
                System.out.println(this.centroides.get(i).getElementos().size());
                soma += this.centroides.get(i).getElementos().size();
            }
            System.out.println("Soma: " + soma);
        }
    }
}
