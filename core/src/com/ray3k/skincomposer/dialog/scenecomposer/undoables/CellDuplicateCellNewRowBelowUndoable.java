package com.ray3k.skincomposer.dialog.scenecomposer.undoables;

import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposer;
import com.ray3k.skincomposer.dialog.scenecomposer.DialogSceneComposerModel;

public class CellDuplicateCellNewRowBelowUndoable implements SceneComposerUndoable {
    private DialogSceneComposerModel.SimCell cell;
    private DialogSceneComposerModel.SimCell newCell;
    private DialogSceneComposer dialog;
    private DialogSceneComposerModel.SimTable table;
    
    public CellDuplicateCellNewRowBelowUndoable() {
        dialog = DialogSceneComposer.dialog;
        
        cell = (DialogSceneComposerModel.SimCell) dialog.simActor;
        table = (DialogSceneComposerModel.SimTable) cell.parent;
        
        newCell = cell.duplicate();
        newCell.column = 0;
        newCell.row = cell.row + 1;
        newCell.parent = table;
    }
    
    @Override
    public void undo() {
        table.cells.removeValue(newCell, true);
        
        for (var currentCell : table.cells) {
            if (currentCell.row >= newCell.row) {
                currentCell.row--;
            }
        }
        
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
        for (var currentCell : table.cells) {
            if (currentCell.row >= newCell.row) {
                currentCell.row++;
            }
        }
    
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
        return "Redo \"Duplicate Cell to New Row Below\"";
    }
    
    @Override
    public String getUndoString() {
        return "Undo \"Duplicate Cell to New Row Below\"";
    }
}
