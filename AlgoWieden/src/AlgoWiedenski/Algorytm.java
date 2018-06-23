/*
 * MIT License
 *
 * Copyright (c) 2018 Michał
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package AlgoWiedenski;

import java.util.ArrayList;
import java.util.Iterator;

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
    private ArrayList<ArrayList<Integer>> _matrixOriginal = new ArrayList<>();
    //2 znalezione zero niezależne, 1 użyty wiersz tego zera, 0 nie wykresolne pole
    private ArrayList<ArrayList<Integer>> _markedZeros = new ArrayList<>();
    //Marked lines with 1, crossing lines with 2, unused with 0.
    private ArrayList<ArrayList<Integer>> _markedLines = new ArrayList<>();
    private ArrayList<IStep> steps = new ArrayList<>();

    public Algorytm(Wegier window) {
        _window = window;
        steps.add(Step0);
        steps.add(Step1);
        steps.add(Step2);
        steps.add(Step3);
        steps.add(Step4);
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

    public ArrayList<ArrayList<Integer>> SetText(String text) {
        ArrayList<ArrayList<Integer>> matrix = new ArrayList<>();
        String[] rows = text.split("\n");
        for (String row : rows) {
            ArrayList<Integer> indexedRow = new ArrayList<>();
            String[] indexs = row.split("\t");
            for (String index : indexs) {
                indexedRow.add(Integer.parseInt(index));
            }
            matrix.add(indexedRow);

        }
        return matrix;
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
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < _matrix.get(rowIndex).size(); ++i) {
            if (_matrix.get(rowIndex).get(i) < min) {
                min = _matrix.get(rowIndex).get(i);
            }
        }
        return min;
    }

    private int MinInCol(int colIndex) {
        int min = Integer.MAX_VALUE;
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
    private boolean FindSolution() {
        int zeroCount = 0;
        int HitZero = 0;
        for (int row = 0; row < _matrix.size(); ++row) {
            for (int column = 0; column < _matrix.get(row).size(); ++column) {
                //jesteśmy w wierszu od lewej do prawej.
                if (_matrix.get(row).get(column) == 0) {
                    ++zeroCount;
                }
            }
        }
        Tickler tickler = new Tickler((long) Math.pow(2, zeroCount));
        String tick;
        int zeroIndex = 0;
        while (tickler.hasNext()) {
            tick = tickler.next();
            System.out.println(tick);
            for (int row = 0; row < _matrix.size(); ++row) {
                for (int column = 0; column < _matrix.get(row).size(); ++column) {
                    //jesteśmy w wierszu od lewej do prawej.
                    if (_matrix.get(row).get(column) == 0) {
                        ++zeroIndex;
                        if (_markedZeros.get(row).get(column) == 0 && tick.charAt(zeroIndex) == '1') {
                            for (int ccolumn = 0; ccolumn < _matrix.get(row).size(); ++ccolumn) {
                                _markedZeros.get(row).set(ccolumn, 1);
                            }
                            for (ArrayList<Integer> rrow : _markedZeros) {
                                rrow.set(column, 1);
                            }
                            _markedZeros.get(row).set(column, 2);
                            ++HitZero;
                            break;
                            //greedy szukamy tylko pierwszego
                        }
                    }
                }

            }
            if (HitZero == _matrix.size()) {
                return true;
            }
            ResetMarkedZeros();
            HitZero = 0;
            zeroIndex = 0;
        }
        return false;
    }

    private void FindNZeros() {
        for (int row = 0; row < _matrix.size(); ++row) {
            for (int column = 0; column < _matrix.get(row).size(); ++column) {
                //jesteśmy w wierszu od lewej do prawej.
                if (_matrix.get(row).get(column) == 0
                        && _markedZeros.get(row).get(column) == 0) {
                    {
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
    }

    private void ReduceUnMaskedValues() {
        int min = Integer.MAX_VALUE;
        for (int row = 0; row < _matrix.size(); ++row) {
            for (int column = 0; column < _matrix.get(row).size(); ++column) {
                if (_markedLines.get(row).get(column) == 0 && _matrix.get(row).get(column) < min) {
                    min = _matrix.get(row).get(column);

                }
            }
        }
        for (int row = 0; row < _matrix.size(); ++row) {
            for (int column = 0; column < _matrix.get(row).size(); ++column) {
                if (_markedLines.get(row).get(column) == 0) {
                    _matrix.get(row).set(column, _matrix.get(row).get(column) - min);
                }
                if (_markedLines.get(row).get(column) == 2) {
                    _matrix.get(row).set(column, _matrix.get(row).get(column) + min);
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
        // przypadków jest 2^n jak do tylu dojdzie to Throw Unvcmputable example!  
        Tickler tickler = new Tickler((long) Math.pow(2, markedZerosCount));
        int MaskedCount = 0;
        String tick;
        while (tickler.hasNext()) {
            //Always draw line that get through found marked zero . Vertically
            // or horizontally.            
            tick = tickler.next();
            System.out.println(tick);
            // 1 |
            // 0 -
            for (int row = 0; row < _matrix.size(); ++row) {
                for (int column = 0; column < _matrix.get(row).size(); ++column) {
                    if (_markedZeros.get(row).get(column) == 2) {
                        if (tick.charAt(MaskedCount) == '0') {
                            for (int ccolumn = 0; ccolumn < _matrix.get(row).size(); ++ccolumn) {
                                _markedLines.get(row).set(ccolumn, _markedLines.get(row).get(ccolumn) + 1);
                            }
                        } else {
                            for (int rrow = 0; rrow < _matrix.size(); ++rrow) {
                                _markedLines.get(rrow).set(column, _markedLines.get(rrow).get(column) + 1);
                            }
                        }
                        ++MaskedCount;
                    }
                }
            }
            //find unmasekd zero. if not all are masked set masked to false.
            masked = true;
            for (int row = 0; row < _matrix.size(); ++row) {
                for (int column = 0; column < _matrix.get(row).size(); ++column) {
                    if (_markedLines.get(row).get(column) == 0
                            && _matrix.get(row).get(column) == 0) {
                        masked = false;
                    }
                }
            }

            if (markedZerosCount == MaskedCount && !masked) {
                MaskedCount = 0;
                ResetMaskedLines();
            }
            if (markedZerosCount == MaskedCount && masked) {
                return;
            }
        }

        throw new Exception(String.format("Solution not found after %d lines coverage attempts", (long) Math.pow(2, markedZerosCount)));
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

    /**
     * Znajdujemy minimalny element w każdym wierszu (yi minimalne). Odejmujemy
     * od danego wiersza tenże element:
     */
    private final IStep Step0 = new IStep() {
        @Override
        public ArrayList<ArrayList<Integer>> actionPerformed(ArrayList<ArrayList<Integer>> matrix) {
            matrix = SetText(_window.getJTP().getText());
            _matrix = SetText(_window.getJTP().getText());
            _matrixOriginal = SetText(_window.getJTP().getText());
            for (int i = 0; i < matrix.size(); ++i) {
                int min = MinInRow(i);
                for (int j = 0; j < matrix.get(i).size(); ++j) {
                    matrix.get(i).set(j, matrix.get(i).get(j) - min);
                }
            }
            return matrix;
        }

    };

    /**
     * W otrzymanej macierzy zaznaczamy z kolei minimalny element w każdej
     * kolumnie(zj minimalne). Również w tym przypadku odejmujemy ten element,
     * ale od danej kolumny
     */
    private final IStep Step1 = new IStep() {
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

    /**
     * wykreśl wszystkie zera macierzy liniami , liczba linii = obecnej liczbie
     * zer niezależnych
     */
    private final IStep Step2 = new IStep() {
        @Override
        public ArrayList<ArrayList<Integer>> actionPerformed(ArrayList<ArrayList<Integer>> matrix) {
            ResetMarkedZeros();
            FindNZeros();
            //else potrzeba zacząć minimalizować 
            if (markedZerosCount == _matrix.size()) {
                return matrix;
            }
            ResetMaskedLines();
            try {
                MakeMarkedLines();
            } catch (Exception ex) {
                _window.getJLMsg().setText(ex.getMessage());
                _window.getJBNext().setEnabled(false);
                return matrix;
            }

            ReduceUnMaskedValues();

            return matrix;
        }
    };

    private final IStep Step3 = new IStep() {
        @Override
        public ArrayList<ArrayList<Integer>> actionPerformed(ArrayList<ArrayList<Integer>> matrix) {
            ResetMarkedZeros();
            if (FindSolution()) {
            } else {
                /**
                 * W przeciwnym wypadku, moglibyśmy zredukować macierz
                 * (powtórnie odjąć minimalny element w wierszach lub/i
                 * kolumnach).
                 */
                currentStep = 2;
            }
            return matrix;
        }
    };

    private final IStep Step4 = new IStep() {
        @Override
        public ArrayList<ArrayList<Integer>> actionPerformed(ArrayList<ArrayList<Integer>> matrix) {
            int solution = 0;
            String msg = "wartość funkcji celu ";

            for (int row = 0; row < _matrix.size(); ++row) {
                for (int column = 0; column < _matrix.get(row).size(); ++column) {
                    if (_markedZeros.get(row).get(column) == 2) {
                        solution += _matrixOriginal.get(row).get(column);
                        msg = msg.concat(String.format("%d + ", _matrixOriginal.get(row).get(column)));
                    }
                }
            }
            msg = msg.substring(0, msg.length() - 2);
            _window.getJLMsg().setText(String.format("%s = %d", msg, solution));
            _window.getJTPMask().setText(TestPaintMZ());
            return _matrixOriginal;
        }

    };

    @Override
    public boolean hasNext() {
        return currentStep < steps.size();
    }

    @Override
    public String next() {
        if (hasNext()) {
            _matrix = steps.get(currentStep).actionPerformed(_matrix);

            currentStep++;
        }
        return Paint();
    }

}
