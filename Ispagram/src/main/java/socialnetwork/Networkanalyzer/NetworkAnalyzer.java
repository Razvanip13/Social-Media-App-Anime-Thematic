package socialnetwork.Networkanalyzer;

import socialnetwork.domain.Tuple;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NetworkAnalyzer {


    List<Tuple<Long,Long>> arce;
    List<Long> noduri;

    int globala=0;
    ArrayList<Tuple<Long,Boolean>> solutie=new ArrayList<>();


    public NetworkAnalyzer(List<Tuple<Long, Long>> tuples, List<Long> users) {
        arce = tuples;
        noduri = users.stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    private void marcheaza(List<Tuple<Long,Boolean>> vizitat,Long nod){

        vizitat.forEach(nodcurent->{

            if(nodcurent.getLeft().equals(nod)){

                nodcurent.setRight(true);
            }

        });
    }

    private void demarcheaza(List<Tuple<Long,Boolean>> vizitat,Long nod){

        vizitat.forEach(nodcurent->{

            if(nodcurent.getLeft().equals(nod)){

                nodcurent.setRight(false);
            }

        });
    }

    private boolean dejaVizitat(Tuple<Long, Boolean> nod) {
        return nod.getRight();
    }

    private boolean adiacent(Long nod1, Long nod2) {
        return arce.contains(new Tuple<>(nod1, nod2)) || arce.contains(new Tuple<>(nod2,nod1));
    }



    private void DFS(List<Tuple<Long, Boolean>> vizitat,Long nod){


        vizitat.forEach(current_node->{

            if(!dejaVizitat(current_node) && adiacent(current_node.getLeft(),nod)){

                marcheaza(vizitat,current_node.getLeft());
                DFS(vizitat,current_node.getLeft());

            }

        });

    }


    public Long getNumberOfComponents(){


        Long total=0L;

        List<Tuple<Long,Boolean>> vizitat= noduri.stream()
                .map(nod->new Tuple<>(nod,false))
                .collect(Collectors.toList());


        Long counter=0L;

        for(Tuple<Long,Boolean> nod: vizitat){

            if(!dejaVizitat(nod)){

                counter++;

                marcheaza(vizitat,nod.getLeft());

                DFS(vizitat,nod.getLeft());
            }

        }





        return counter;
    }

    private void copy_solution(List<Tuple<Long, Boolean>> vizitat){

        solutie.clear();

        solutie.addAll(vizitat);

    }


    private void verifica_drumuri(List<Tuple<Long, Boolean>> vizitat, Long nod, int count){


        if(globala<count){

            globala=count;

            copy_solution(vizitat);
        }


        vizitat.forEach(current_node->{

            if(!dejaVizitat(current_node) && adiacent(current_node.getLeft(),nod)){

                marcheaza(vizitat,current_node.getLeft());

                verifica_drumuri(vizitat,current_node.getLeft(),count+1);

                demarcheaza(vizitat,current_node.getLeft());

            }


        });


    }

    private void genereaza_componente(List<Tuple<Long, Boolean>> vizitat, Long nod, List<Tuple<Long, Boolean>> generata){


        vizitat.forEach(current_node->{

            if(!dejaVizitat(current_node) && adiacent(current_node.getLeft(),nod)){

                generata.add(new Tuple<>(current_node.getLeft(),current_node.getRight()));

                marcheaza(vizitat,current_node.getLeft());
                genereaza_componente(vizitat,current_node.getLeft(),generata);

            }

        });

    }

    public List<Long> biggestCommunity(){

        List<Tuple<Long,Boolean>> vizitat= noduri.stream()
                .map(nod->new Tuple<>(nod,false))
                .collect(Collectors.toList());

        globala=0;
        solutie.clear();



        vizitat.forEach(nod->{

            if(!dejaVizitat(nod)) {

                ArrayList<Tuple<Long, Boolean>> generata = new ArrayList<>();


                generata.add(new Tuple<>(nod.getLeft(), nod.getRight()));


                marcheaza(vizitat,nod.getLeft());

                genereaza_componente(vizitat, nod.getLeft(), generata);


                //generata.forEach(System.out::print);
               // System.out.println();

                generata.forEach(conex_nod->{

                    marcheaza(generata, conex_nod.getLeft());

                    verifica_drumuri(generata, conex_nod.getLeft(), 1);

                    demarcheaza(generata, conex_nod.getLeft());
                });
            }

        });


        return solutie.stream()
                .map(Tuple::getLeft)
                .collect(Collectors.toList());

    }


}
