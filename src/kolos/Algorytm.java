/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kolos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author michal
 */
public class Algorytm implements Iterator<String> {

    private Wegier _window;
    private boolean ended = false;
    private int currentStep = 0;
    private int markedZerosCount = 0;
    private ArrayList<ArrayList<Integer>> _matrix = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> _markedZeros = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> _markedLines = new ArrayList<>();
    private ArrayList<IStep> steps = new ArrayList<>();

    public Algorytm(Wegier window) {
        _window = window;
        steps.add(Step1);
        steps.add(Step2);
        steps.add(Step3);
    }

    public String TestPaintMZ() {
        String text = "";
        for (ArrayList<Integer> row : _markedZeros) {
            String strRow = "";
            for (int indexes : row) {
                strRow = strRow.concat(String.format("%d\t", indexes));
            }
            text = text.concat(String.format("%s\n", strRow));
        }
        return text;
    }

    public String TestPaintML() {
        String text = "";
        for (ArrayList<Integer> row : _markedLines) {
            String strRow = "";
            for (int indexes : row) {
                strRow = strRow.concat(String.format("%d\t", indexes));
            }
            text = text.concat(String.format("%s\n", strRow));
        }
        return text;
    }

    public void SetText(String text) {
        _matrix = new ArrayList<ArrayList<Integer>>();
        String[] rows = text.split("\n");
        for (String row : rows) {
            ArrayList<Integer> indexedRow = new ArrayList<>();
            String[] indexs = row.split("\t");
            for (String index : indexs) {
                indexedRow.add(Integer.parseInt(index));
            }
            _matrix.add(indexedRow);

        }
    }

    /**
     *
     * @return
     */
    public String Paint() {
        String text = "";
        for (ArrayList<Integer> row : _matrix) {
            String strRow = "";
            for (int indexes : row) {
                strRow = strRow.concat(String.format("%d\t", indexes));
            }
            text = text.concat(String.format("%s\n", strRow));
        }
        return text;
    }

    private int MinInRow(int rowIndex) {
        int min = 9999;
        for (int i = 0; i < _matrix.get(rowIndex).size(); ++i) {
            if (_matrix.get(rowIndex).get(i) < min) {
                min = _matrix.get(rowIndex).get(i);
            }
        }
        return min;
    }

    private int MinInCol(int colIndex) {
        int min = 9999;
        for (ArrayList<Integer> row : _matrix) {
            if (min > row.get(colIndex)) {
                min = row.get(colIndex);
            }
        }

        return min;

    }

    /**
     * robimy maskę. jezeli kolumna ma zero wstawiamy w calej kolumnie true - 1
     * i w wierszu jaki znalezlismy to zero. kolejna kolumna index jezeli dana
     * kolumna ma zero w miejscu nie oznaczonym true znalezlismykolejne zero
     * neizlaezne i oznaczamy kolumne true i wiersz. jezeli wszystkie wiersze
     * lub kolumny są oznaczone true i niema wiecej zer w dostepnych miejscach
     * spradzamy pkt przecięcia potrzeba gdzies przechowac dane zero jakie
     * wymazalo wiersz na true koordynaty znalezionego zera wbijammy 2
     */
    private void FindRowNZero() {
        for (int row = 0; row < _matrix.size(); ++row) {
            for (int column = 0; column < _matrix.get(row).size(); ++column) {
                //jesteśmy w wierszu od lewej do prawej.
                if (_matrix.get(row).get(column) == 0
                        && _markedZeros.get(row).get(column) == 0) {
                    for (int ccolumn = 0; ccolumn < _matrix.get(row).size(); ++ccolumn) {
                        _markedZeros.get(row).set(ccolumn, 1);
                    }
                    for (ArrayList<Integer> rrow : _markedZeros) {
                        rrow.set(column, 1);
                    }
                    _markedZeros.get(row).set(column, 2);
                    ++markedZerosCount;
                    break;
                    //greedy szukamy tylko pierwszego

                }
            }
        }

    }

    /**
     * tak aby liczba linii była minimalna, jeżeli liczba linii = n STOP jak
     * zero zostaje przykryte to w masce jest na 1.
     */
    private void MakeMarkedLines() throws Exception {

        boolean masked = false;
        int fuse = (int) Math.pow(2, markedZerosCount); // przypadków jest 2^n jak do tylu dojdzie to Throw Unvcmputable example!  
        int TryedMasekd = 0;
        int MaskedCount = 0;
        int StartColumn = 0;
        while (!masked) {
            //Always draw line that get through found marked zero . Vertically
            // or horizontally.
            for (int row = 0; row < _matrix.size(); ++row) {
                for (int column = 0; column < _matrix.get(row).size(); ++column) {
                    if (_markedZeros.get(row).get(column) == 2) {
                        for (int ccolumn = 0; ccolumn < _matrix.get(row).size(); ++ccolumn) {
                            _markedLines.get(row).set(ccolumn, 1);
                        }
                    }
                }
            }

            //find unmasekd zero. if all are masked set masked =truse.
            if (markedZerosCount == MaskedCount && !masked) {
                MaskedCount = 0;
                ResetMaskedLines();
                ++StartColumn;
            }
            if (TryedMasekd == fuse) {
                throw new Exception(String.format("Solution not found after %d lines coverage attempts", fuse));
            }
            ++TryedMasekd;
        }
    }   

    private void ResetMaskedLines() {
        _markedLines = new ArrayList<>();
        for (ArrayList<Integer> row : _matrix) {
            ArrayList<Integer> indexedRow = new ArrayList<>();

            for (Integer index : row) {
                indexedRow.add(0);
            }
            _markedLines.add(indexedRow);

        }
    }

    private void ResetMarkedZeros() {
        _markedZeros = new ArrayList<>();
        markedZerosCount = 0;
        for (ArrayList<Integer> row : _matrix) {
            ArrayList<Integer> indexedRow = new ArrayList<>();

            for (Integer index : row) {
                indexedRow.add(0);
            }
            _markedZeros.add(indexedRow);

        }
    }

    private final IStep Step1 = new IStep() {
        @Override
        public ArrayList<ArrayList<Integer>> actionPerformed(ArrayList<ArrayList<Integer>> matrix) {
            for (int i = 0; i < matrix.size(); ++i) {
                int min = MinInRow(i);
                for (int j = 0; j < matrix.get(i).size(); ++j) {
                    matrix.get(i).set(j, matrix.get(i).get(j) - min);
                }
            }
            return matrix;
        }

    };

    private final IStep Step2 = new IStep() {
        @Override
        public ArrayList<ArrayList<Integer>> actionPerformed(ArrayList<ArrayList<Integer>> matrix) {
            ArrayList<Integer> minCol = new ArrayList<>();
            for (int coulmn = 0; coulmn < matrix.get(0).size(); ++coulmn) {//po kolumnach
                minCol.add(MinInCol(coulmn));
            }
            for (int row = 0; row < matrix.size(); ++row) {
                for (int column = 0; column < matrix.get(row).size(); ++column) {
                    matrix.get(row).set(column, matrix.get(row).get(column) - minCol.get(column));
                }
            }

            return matrix;
        }
    };

    private final IStep Step3 = new IStep() {
        @Override
        public ArrayList<ArrayList<Integer>> actionPerformed(ArrayList<ArrayList<Integer>> matrix) {
            ResetMarkedZeros();
            FindRowNZero();
            ResetMaskedLines();
            try {
                MakeMarkedLines();
            } catch (Exception ex) {
                _window.getJLMsg().setText(ex.getMessage());
                _window.getJBNext().setEnabled(false);
                return matrix;
            }

            return matrix;
        }
    };

    @Override
    public boolean hasNext() {
        return currentStep < steps.size();
    }

    @Override
    public String next() {
        if (hasNext()) {
            try {
                _matrix = steps.get(currentStep).actionPerformed(_matrix);
            } catch (Exception ex) {
                Logger.getLogger(Algorytm.class.getName()).log(Level.SEVERE, null, ex);
            }
            currentStep++;
        }
        return Paint();
    }

}
