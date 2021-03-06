package LEARN_HERE.Tutorials.PDEexample;

import Framework.GridsAndAgents.AgentGrid2D;
import Framework.GridsAndAgents.AgentPT2D;
import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.GridWindow;
import Framework.Gui.OpenGL2DWindow;
import Framework.Rand;
import Framework.Util;

class ExampleCell extends AgentPT2D<ExampleGrid> {
    int color;

    public void Init(){
        this.color=Util.RGB(G.rng.Double(), G.rng.Double(), G.rng.Double());
    }

    public void StepCell(double dieProb,double divProb){
        if(G.oxygen.Get(Isq())<0.7|| G.rng.Double()<dieProb){
            //cell will die
            Dispose();
            return;
        }
        G.oxygen.Mul(Isq(), 0.98 -1);
        if(G.rng.Double()<divProb&& G.PopAt(Isq())<5){
            G.NewAgentPT(Xpt(),Ypt()).Init();
        }
        //cell will move
        G.rng.RandomPointInCircle(0.5, G.moveCoords);
        MoveSafePT(Xpt()+ G.moveCoords[0],Ypt()+ G.moveCoords[1]);
    }
}

public class ExampleGrid extends AgentGrid2D<ExampleCell> {
    Rand rng=new Rand();
    int[]divHood=Util.VonNeumannHood(false);
    double[]moveCoords=new double[2];
    PDEGrid2D oxygen;

    public ExampleGrid(int x, int y) {
        super(x, y, ExampleCell.class);
        oxygen =new PDEGrid2D(x,y);
        oxygen.SetAll(1);
        oxygen.Update();
    }
    public void Step(double dieProb, double divProb){
        for(ExampleCell cell:this){
            cell.StepCell(dieProb,divProb);
        }
        for (int i = 0; i < length; i++) {
            if(oxygen.Get(i)<1){
                oxygen.Add(i,0.02);
            }
        }
        oxygen.Diffusion(0.1);
        oxygen.Update();
    }

    public void DrawOxygen(GridWindow win){
        for (int i = 0; i < length; i++) {
            win.SetPix(i,Util.HeatMapRGB(oxygen.Get(i)));
        }
    }

    public void DrawModel(OpenGL2DWindow win){
        win.Clear(Util.BLACK);
        for (ExampleCell cell : this) {
            win.Circle(cell.Xpt(),cell.Ypt(),0.5,cell.color);
        }
        win.Update();
    }

    public static void main(String[]args){
        int x=100;
        int y=100;
        int timesteps=1000;
        double dieProb=0.1;
        double divProb=0.2;

        OpenGL2DWindow win=new OpenGL2DWindow("2D",500,500,x,y);
        GridWindow oxygenWin=new GridWindow("oxygen",x,y,5);
        ExampleGrid model=new ExampleGrid(x,y);

        //initialize model

        for (int i = 0; i < timesteps; i++) {
            win.TickPause(20);
            if(model.Pop()==0){
                model.NewAgentPT(model.xDim/2.0,model.yDim/2.0).Init();
            }
            //model step
            model.Step(dieProb,divProb);

            //draw
            model.DrawModel(win);
            model.DrawOxygen(oxygenWin);
        }
    }
    //can you increase the diffusion rate above the numerically stable rate constant of 0.25?
}

