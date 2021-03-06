package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CellAddCellBelowUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCell cell;
    private DialogSceneComposerModel.SimCell newCell;
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel.SimTable table;
    
    public CellAddCellBelowUndoable() {
        dialog = DialogSceneComposer.dialog;
        
        cell = (DialogSceneComposerModel.SimCell) dialog.simActor;
        table = (DialogSceneComposerModel.SimTable) cell.parent;
        
        newCell = new DialogSceneComposerModel.SimCell();
        newCell.column = table.getColumns(cell.row + 1);
        newCell.row = cell.row + 1;
        newCell.parent = table;
    }
    
    @Override
    public void undo() {
        table.cells.removeValue(newCell, true);
        table.sort();
        
        if (dialog.simActor != cell) {
            dialog.simActor = cell;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public void redo() {
        table.cells.add(newCell);
        table.sort();
        
        if (dialog.simActor != newCell) {
            dialog.simActor = newCell;
            dialog.populateProperties();
        }
        dialog.populatePath();
        dialog.model.updatePreview();
    }
    
    @Override
    public String getRedoString() {
        return "Redo \"Add Row Above Cell\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Add Row Above Cell\"";
    }
}
