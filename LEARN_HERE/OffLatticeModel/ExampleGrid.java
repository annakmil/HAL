package LEARN_HERE.OffLatticeModel;

import Framework.GridsAndAgents.AgentGrid2D;
import Framework.GridsAndAgents.AgentPT2D;
import Framework.Gui.OpenGL2DWindow;
import Framework.Rand;
import Framework.Util;

class ExampleCell extends AgentPT2D<ExampleGrid> {
    int color;

    public void Init(){
        this.color=Util.RGB(G.rng.Double(), G.rng.Double(), G.rng.Double());
    }

    public void StepCell(double dieProb,double divProb){
        if(G.rng.Double()<dieProb){
            //cell will die
            Dispose();
            return;
        }
        if(G.rng.Double()<divProb&& G.PopAt(Isq())<5){
            G.NewAgentPT(Xpt(),Ypt()).Init();
        }
        //cell will move
        G.rng.RandomPointInCircle(0.5, G.moveCoords);
        MoveSafePT(Xpt()+ G.moveCoords[0],Ypt()+ G.moveCoords[1]);
    }
}

public class ExampleGrid extends AgentGrid2D<ExampleCell> {
    Rand rng = new Rand();
    int[] divHood = Util.VonNeumannHood(false);
    double[] moveCoords = new double[2];

    public ExampleGrid(int x, int y) {
        super(x, y, ExampleCell.class);
    }

    public void Step(double dieProb, double divProb) {
        for (ExampleCell cell : this) {
            cell.StepCell(dieProb, divProb);
        }
    }

    public void DrawModel(OpenGL2DWindow win) {
        win.Clear(Util.BLACK);
        for (ExampleCell cell : this) {
            win.Circle(cell.Xpt(), cell.Ypt(), 0.5, cell.color);
        }
        win.Update();
    }

    public static void main(String[] args) {
        int x = 100;
        int y = 100;
        int timesteps = 1000;
        double dieProb = 0.1;
        double divProb = 0.2;
        OpenGL2DWindow win = new OpenGL2DWindow("2D", 500, 500, x, y);

        ExampleGrid model = new ExampleGrid(x, y);

        //initialize model

        for (int i = 0; i < timesteps; i++) {
            if (model.Pop() == 0) {
                model.NewAgentPT(model.xDim / 2.0, model.yDim / 2.0).Init();
            }
            //model step
            model.Step(dieProb, divProb);

            model.DrawModel(win);

            //can you use iThread to vary the consumption rate?
        }
    }
}

