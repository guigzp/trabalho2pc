import java.util.ArrayList;
import java.util.List;

public class Centroide {
    private ArrayList<Integer> coordenadas = new ArrayList<>();
    private ArrayList<Elemento> elementos = new ArrayList<>();

    public Centroide(ArrayList<Integer> coordenadas) {
        this.coordenadas = (ArrayList) coordenadas.clone();
    }

    public Centroide() {

    }

    public void addElemento(Elemento e) {
        this.elementos.add(e);
    }

    public void setCoordenadas(ArrayList<Integer> coordenadas) {
        this.coordenadas = coordenadas;
    }

    public ArrayList<Integer> getCoordenadas() {
        return coordenadas;
    }

    public ArrayList<Elemento> getElementos() {
        return elementos;
    }

    public void setElementos(ArrayList<Elemento> elementos) {
        this.elementos = elementos;
    }

    public Boolean moveCentroide() {
        int qtdCoordenadas = this.coordenadas.size();
        ArrayList<Integer> aux = new ArrayList<>();
        int soma = 0;
        for(int i = 0; i < qtdCoordenadas; i++) {
            for(Elemento e: this.elementos) {
                soma += e.getCoordenadas().get(i);
            }
            soma /= this.elementos.size();
            aux.add(soma);
            soma = 0;
        }
        if(this.coordenadas.equals(aux)) {
            return false;
        } else {
            this.coordenadas = aux;
            return true;
        }
    }

}
