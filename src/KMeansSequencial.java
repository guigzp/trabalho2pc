import java.util.ArrayList;

public class KMeansSequencial {
    private ArrayList<Centroide> centroides;
    private ArrayList<Elemento> elementos;
    private int iteracoes = 0;

    public int getIteracoes() {
        return iteracoes;
    }
    public void incrementaIteracaoes(){
        this.iteracoes ++;
    }

    public KMeansSequencial(ArrayList<Centroide> centroides, ArrayList<Elemento> elementos) {
        this.centroides = (ArrayList) centroides.clone();
        this.elementos = (ArrayList) elementos.clone();
    }

    public ArrayList<Centroide> getCentroides() {
        return centroides;
    }

    public ArrayList<Elemento> getElementos() {
        return elementos;
    }

    public void atribuiConjuntos(){
        for (Elemento e : this.elementos){
            melhorCentroide(e);
        }
    }

    public void melhorCentroide(Elemento e){
        Integer menorDistancia = Integer.MAX_VALUE;
        Centroide melhor = new Centroide();
        for (Centroide c : this.centroides){
            int aux = calculaDistancia(e, c);
            if (menorDistancia > aux){
                menorDistancia = aux;
                melhor = c;
            }
        }
        e.setIndiceCentroide( this.centroides.indexOf(melhor));
    }

    public int calculaDistancia(Elemento e, Centroide c){
        int qtd = e.getCoordenadas().size();
        int somatorio = 0;
        for (int i = 0; i < qtd; i++){
            somatorio += Math.pow(e.getCoordenadas().get(i) - c.getCoordenadas().get(i), 2.0);
        }
        return (int) Math.sqrt(somatorio);
    }


    public void executa(){
        Boolean moveu;
        do{
            incrementaIteracaoes();
            for (Centroide c : this.centroides){
                c.getElementos().clear();
            }
            moveu = false;
            atribuiConjuntos();
            for(Elemento e : this.elementos){
                this.centroides.get(e.getIndiceCentroide()).addElemento(e);
            }
            for(Centroide c : this.centroides){
                if(c.moveCentroide()){
                    moveu = true;
                }
            }
        }while(moveu);
    }
}
