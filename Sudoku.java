class Sudoku{
    int N;
    int[][] grid;

    public Sudoku(int N){
        this.N = N ;
        grid = new int[N][N];
    }

    public void affichage(){
        int i,j;
        System.out.print("-----------------------------");
        for(i=0;i<N;i++){
            System.out.println("\n");
            for(j=0;j<N;j++){
                System.out.print(grid[i][j]+ " ");
            }
        }
        System.out.println("\n-----------------------------");
    }

    public boolean finish(){
        int i,j;
        for(i=0;i<N;i++){
            for(j=0;j<N;j++){
                if(grid[i][j] == 0){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean not_in_line(int x, int y, int value){
        int i;
        for(i=0; i<N ; i++){
            if(grid[i][y] == value){
                return false;
            }
        }
        return true;
    }

    public boolean not_in_column(int x, int y, int value){
        int i;
        for(i=0 ; i<N ; i++){
            if(grid[x][i] == value){
                return false;
            }
        }
        return true;
    }

    public boolean not_in_box(int x, int y, int value) {
        int boxStartRow = x - x % 3;
        int boxStartCol = y - y % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[boxStartRow + i][boxStartCol + j] == value) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean is_valid(int x, int y, int value){
        boolean line = not_in_line(x, y, value);
        boolean column = not_in_column(x, y, value);
        boolean box = not_in_box(x, y, value);
        return line && column && box;
    }

    public boolean solve(int x,int y){
        if(x==N){
            return true;
        }
        else if(y==N){
            return solve(x+1,0);
        }
        else if(grid[x][y] != 0)
            return solve(x,y+1);
        else{
            for(int i=1 ; i<=N ; i++){
                if(is_valid(x, y, i)){
                    grid[x][y] = i;
                    if(solve(x,y+1)){
                        return true;
                    }
                    grid[x][y] = 0;
                }
            }
            return false;
        }
    }

    public boolean verify() {
        // Vérifier chaque ligne, chaque colonne et chaque boîte 3x3
        for (int i = 0; i < N; i++) {
            if (!verifyLine(i) || !verifyColumn(i) || !verifyBox(i - i % 3, i - i % 3)) {
                return false;
            }
        }
        return true;
    }

    private boolean verifyLine(int line) {
        boolean[] seen = new boolean[N + 1];
        for (int i = 0; i < N; i++) {
            int num = grid[line][i];
            if (num < 1 || num > N || seen[num]) {
                return false;
            }
            seen[num] = true;
        }
        return true;
    }

    private boolean verifyColumn(int column) {
        boolean[] seen = new boolean[N + 1];
        for (int i = 0; i < N; i++) {
            int num = grid[i][column];
            if (num < 1 || num > N || seen[num]) {
                return false;
            }
            seen[num] = true;
        }
        return true;
    }

    private boolean verifyBox(int startRow, int startCol) {
        boolean[] seen = new boolean[N + 1];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int num = grid[row + startRow][col + startCol];
                if (num < 1 || num > N || seen[num]) {
                    return false;
                }
                seen[num] = true;
            }
        }
        return true;
    }
}
