/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2016 Raymond Buckley
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
 ******************************************************************************/
package com.ray3k.skincomposer.panel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.MenuList;
import com.ray3k.skincomposer.data.AtlasData;
import com.ray3k.skincomposer.data.FontData;
import com.ray3k.skincomposer.data.JsonData;
import com.ray3k.skincomposer.data.ProjectData;
import com.ray3k.skincomposer.dialog.DialogError;
import com.ray3k.skincomposer.utils.Utils;
import java.io.File;
import java.util.List;
import javafx.stage.FileChooser;

public class PanelMenuBar {
    private Skin skin;
    private Stage stage;
    private TextButton undoButton, redoButton, recentFilesButton;
    private static PanelMenuBar instance;
    
    public PanelMenuBar(final Table table, final Skin skin, final Stage stage) {
        instance = this;
        
        this.skin = skin;
        this.stage = stage;
        final Array<TextButton> menuButtons = new Array<>();
        
        table.defaults().padTop(1.0f).padBottom(1.0f);
        table.setBackground("dark-orange");
        
        TextButton textButton = new TextButton("File", skin, "menu");
        Table menuItemTable = new Table();
        menuItemTable.defaults().growX();
        
        TextButton menuItemTextButton = new TextButton("New", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.add(new Label(getShortcutNames().get("new"), skin, "shortcut")).padLeft(5.0f);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                newDialog();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Open...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.add(new Label(getShortcutNames().get("open"), skin, "shortcut")).padLeft(5.0f);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                openDialog();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        recentFilesButton = new TextButton("Recent Files...", skin, "menu-item");
        recentFilesButton.getLabel().setAlignment(Align.left);
        recentFilesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                recentFilesDialog();
            }
        });
        if (ProjectData.instance().getRecentFiles().size == 0) {
            recentFilesButton.setDisabled(true);
        }
        menuItemTable.add(recentFilesButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Save", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.add(new Label(getShortcutNames().get("save"), skin, "shortcut")).padLeft(5.0f);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                save(null);
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Save As...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.add(new Label(getShortcutNames().get("save as"), skin, "shortcut")).padLeft(5.0f);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                saveAsDialog(null);
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Import...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                importDialog();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Export...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                exportDialog();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Exit", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance().showCloseDialog();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        final MenuList menuList1 = new MenuList(textButton, menuItemTable);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (((TextButton) actor).isChecked()) {
                    menuList1.show(stage);
                } else {
                    menuList1.hide();
                }
            }
        });
        table.add(textButton).padLeft(1.0f);
        menuButtons.add(textButton);
        
        textButton = new TextButton("Edit", skin, "menu");
        menuItemTable = new Table();
        menuItemTable.defaults().growX();
        undoButton = new TextButton("Undo", skin, "menu-item");
        undoButton.getLabel().setAlignment(Align.left);
        undoButton.add(new Label(getShortcutNames().get("undo"), skin, "shortcut")).padLeft(5.0f);
        undoButton.setDisabled(true);
        undoButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance().undo();
            }
        });
        menuItemTable.add(undoButton);
        
        menuItemTable.row();
        redoButton = new TextButton("Redo", skin, "menu-item");
        redoButton.getLabel().setAlignment(Align.left);
        redoButton.add(new Label(getShortcutNames().get("redo"), skin, "shortcut")).padLeft(5.0f);
        redoButton.setDisabled(true);
        redoButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance().redo();
            }
        });
        menuItemTable.add(redoButton);
        
        final MenuList menuList2 = new MenuList(textButton, menuItemTable);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (((TextButton) actor).isChecked()) {
                    menuList2.show(stage);
                } else {
                    menuList2.hide();
                }
            }
        });
        table.add(textButton);
        menuButtons.add(textButton);
        
        textButton = new TextButton("Project", skin, "menu");
        menuItemTable = new Table();
        
        menuItemTable.defaults().growX();
        menuItemTextButton = new TextButton("Settings...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance().showDialogSettings();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTable.defaults().growX();
        menuItemTextButton = new TextButton("Colors...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance().showDialogColors();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Fonts...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance().showDialogFonts();
            }
        });
        menuItemTable.add(menuItemTextButton);
        
        menuItemTable.row();
        menuItemTextButton = new TextButton("Drawables...", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance().showDialogDrawables();
            }
        });
        menuItemTable.add(menuItemTextButton);
        final MenuList menuList3 = new MenuList(textButton, menuItemTable);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (((TextButton) actor).isChecked()) {
                    menuList3.show(stage);
                } else {
                    menuList3.hide();
                }
            }
        });
        table.add(textButton);
        menuButtons.add(textButton);
        
        textButton = new TextButton("Help", skin, "menu");
        menuItemTable = new Table();
        menuItemTable.defaults().growX();
        menuItemTextButton = new TextButton("About", skin, "menu-item");
        menuItemTextButton.getLabel().setAlignment(Align.left);
        menuItemTextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Main.instance().showDialogAbout();
            }
        });
        menuItemTable.add(menuItemTextButton);
        final MenuList menuList4 = new MenuList(textButton, menuItemTable);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if (((TextButton) actor).isChecked()) {
                    menuList4.show(stage);
                } else {
                    menuList4.hide();
                }
            }
        });
        table.add(textButton);
        menuButtons.add(textButton);
        table.add().growX();

        //deselect menu buttons if escape is pressed or if stage is clicked anywhere else
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    for (TextButton button : menuButtons) {
                        button.setChecked(false);
                    }
                }
                return false;
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                for (TextButton textButton : menuButtons) {
                    if (!textButton.isAscendantOf(event.getTarget())) {
                        textButton.setChecked(false);
                    }
                }
                return false;
            }
        });
    }

    public static PanelMenuBar instance() {
        return instance;
    }

    public TextButton getUndoButton() {
        return undoButton;
    }

    public TextButton getRedoButton() {
        return redoButton;
    }

    public TextButton getRecentFilesButton() {
        return recentFilesButton;
    }
    
    public void newDialog() {
        if (!ProjectData.instance().areChangesSaved() && !ProjectData.instance().isNewProject()) {
            yesNoCancelDialog("Save Changes?",
                    "Do you want to save changes to the existing project?"
                            + "\nAll unsaved changes will be lost.",
                    (int selection) -> {
                        if (selection == 0) {
                            save(() -> {
                                ProjectData.instance().clear();
                            });
                        } else if (selection == 1) {
                            ProjectData.instance().clear();
                        }
                    });
        } else {
            ProjectData.instance().clear();
        }
    }
    
    private void yesNoCancelDialog(String title, String text, ConfirmationListener listener) {
        Dialog dialog = new Dialog(title, skin, "dialog") {
            @Override
            protected void result(Object object) {
                listener.selected((int) object);
            }
        };
        Label label = new Label(text, skin);
        label.setAlignment(Align.center);
        dialog.text(label);
        dialog.getContentTable().getCells().first().pad(10.0f);
        dialog.button("Yes", 0);
        dialog.button("No", 1);
        dialog.button("Cancel", 2);
        dialog.key(Keys.ESCAPE, 2);
        dialog.show(stage);
    }
    
    private void yesNoDialog(String title, String text, ConfirmationListener listener) {
        Dialog dialog = new Dialog(title, skin, "dialog") {
            @Override
            protected void result(Object object) {
                listener.selected((int) object);
            }
        };
        Label label = new Label(text, skin);
        label.setAlignment(Align.center);
        dialog.text(label);
        dialog.getContentTable().getCells().first().pad(10.0f);
        dialog.button("Yes", 0);
        dialog.button("No", 1);
        dialog.key(Keys.ESCAPE, 1);
        dialog.show(stage);
    }
    
    private interface ConfirmationListener {
        public void selected(int selection);
    }
    
    public void openDialog() {
        Runnable runnable = () -> {
            String defaultPath = "";

            if (ProjectData.instance().getLastDirectory() != null) {
                FileHandle fileHandle = new FileHandle(defaultPath);
                if (fileHandle.exists()) {
                    defaultPath = ProjectData.instance().getLastDirectory();
                }
            }

            String[] filterPatterns = {"*.scmp"};

            File file = Main.instance().getDesktopWorker().openDialog("Open skin file...", defaultPath, filterPatterns, "Skin Composer files");
            if (file != null) {
                FileHandle fileHandle = new FileHandle(file);
                ProjectData.instance().load(fileHandle);
            }
        };
        
        if (!ProjectData.instance().areChangesSaved() && !ProjectData.instance().isNewProject()) {
            yesNoCancelDialog("Save Changes?",
                    "Do you want to save changes to the existing project?"
                    + "\nAll unsaved changes will be lost.",
                    (int selection) -> {
                        if (selection == 0) {
                            save(runnable);
                        } else if (selection == 1) {
                            Main.instance().showDialogLoading(runnable);
                        }
                    });
        } else {
            Main.instance().showDialogLoading(runnable);
        }
    }
    
    private void recentFilesDialog() {
        SelectBox<String> selectBox = new SelectBox(skin);
        Dialog dialog = new Dialog("Recent Files...", skin) {
            @Override
            protected void result(Object object) {
                super.result(object);
                if ((boolean) object) {
                    if (selectBox.getSelected() != null) {
                        FileHandle file = new FileHandle(selectBox.getSelected());
                        if (file.exists()) {
                            ProjectData.instance().load(file);
                        }
                    }
                }
            }
        };
        
        selectBox.setItems(ProjectData.instance().getRecentFiles());
        
        dialog.text("Select a file to open");
        dialog.getContentTable().row();
        dialog.getContentTable().add(selectBox);
        dialog.button("OK", true).key(Keys.ENTER, true);
        dialog.button("Cancel", false).key(Keys.ESCAPE, false);
        dialog.show(stage);
    }
    
    public void save(Runnable runnable) {
        if (ProjectData.instance().getSaveFile() != null) {
            
            Main.instance().showDialogLoading(() -> {
                ProjectData.instance().save();
                if (runnable != null) {
                    runnable.run();
                }
            });
        } else {
            saveAsDialog(runnable);
        }
    }
    
    public void saveAsDialog(Runnable runnable) {
        Main.instance().showDialogLoading(() -> {
            String defaultPath = "";

            if (ProjectData.instance().getLastDirectory() != null) {
                FileHandle fileHandle = new FileHandle(defaultPath);
                if (fileHandle.exists()) {
                    defaultPath = ProjectData.instance().getLastDirectory();
                }
            }

            String[] filterPatterns = {"*.scmp"};

            File file = Main.instance().getDesktopWorker().saveDialog("Save skin file as...", defaultPath, filterPatterns, "Skin Composer files");
            if (file != null) {
                FileHandle fileHandle = new FileHandle(file);
                if (fileHandle.extension() == null || !fileHandle.extension().equals(".scmp")) {
                    fileHandle = fileHandle.sibling(fileHandle.nameWithoutExtension() + ".scmp");
                }
                ProjectData.instance().save(fileHandle);
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
    }
    
    public void importDialog() {
        Main.instance().showDialogLoading(() -> {
            String defaultPath = "";

            if (ProjectData.instance().getLastDirectory() != null) {
                FileHandle fileHandle = new FileHandle(defaultPath);
                if (fileHandle.exists()) {
                    defaultPath = ProjectData.instance().getLastDirectory();
                }
            }

            String[] filterPatterns = {"*.json"};

            File file = Main.instance().getDesktopWorker().openDialog("Import skin...", defaultPath, filterPatterns, "Json files");
            if (file != null) {
                FileHandle fileHandle = new FileHandle(file);
                ProjectData.instance().setLastDirectory(fileHandle.parent().path());
                try {
                    JsonData.getInstance().readFile(fileHandle);
                    PanelClassBar.instance.populate();
                    PanelStyleProperties.instance.populate(PanelClassBar.instance.getStyleSelectBox().getSelected());
                    AtlasData.getInstance().atlasCurrent = false;
                    PanelPreviewProperties.instance.produceAtlas();
                    PanelPreviewProperties.instance.populate();
                } catch (Exception e) {
                    Gdx.app.error(getClass().getName(), "Error attempting to import JSON", e);
                    DialogError.showError("Import Error...", "Error while attempting to import a skin.\nPlease check that all files exist.\n\nOpen log?");
                }
            }
        });
    }
    
    public void exportDialog() {
        Main.instance().showDialogLoading(() -> {
            String defaultPath = "";

            if (ProjectData.instance().getLastDirectory() != null) {
                FileHandle fileHandle = new FileHandle(defaultPath);
                if (fileHandle.exists()) {
                    defaultPath = ProjectData.instance().getLastDirectory();
                }
            }

            String[] filterPatterns = {"*.json"};

            File file = Main.instance().getDesktopWorker().saveDialog("Export skin...", defaultPath, filterPatterns, "Json files");
            if (file != null) {
                FileHandle fileHandle = new FileHandle(file);
                if (fileHandle.extension() == null || !fileHandle.extension().equals(".json")) {
                    fileHandle = fileHandle.sibling(fileHandle.nameWithoutExtension() + ".json");
                }
                ProjectData.instance().setLastDirectory(fileHandle.parent().path());
                JsonData.getInstance().writeFile(fileHandle);
                
                try {
                    AtlasData.getInstance().writeAtlas(fileHandle.parent().child(fileHandle.nameWithoutExtension() + ".atlas"));
                } catch (Exception ex) {
                    Gdx.app.error(PanelMenuBar.class.getName(), "Error while writing texture atlas", ex);
                    DialogError.showError("Atlas Error...", "Error while writing texture atlas.\n\nOpen log?");
                }
                
                for (FontData font : JsonData.getInstance().getFonts()) {
                    if (!font.file.parent().equals(fileHandle.parent())) {
                        font.file.copyTo(fileHandle.parent());
                    }
                }
            }
        });
    }
    
    private static ObjectMap<String, String> shortcutNames;
    
    private static ObjectMap<String, String> getShortcutNames() {
        if (shortcutNames == null) {
            shortcutNames = new ObjectMap();
            
            if (Utils.isMac()) {
                shortcutNames.put("new", "⌘+N");
                shortcutNames.put("open", "⌘+O");
                shortcutNames.put("save", "⌘+S");
                shortcutNames.put("save as", "Shift+⌘+S");
                shortcutNames.put("undo", "⌘+Z");
                shortcutNames.put("redo", "⌘+Y");
            } else {
                shortcutNames.put("new", "Ctrl+N");
                shortcutNames.put("open", "Ctrl+O");
                shortcutNames.put("save", "Ctrl+S");
                shortcutNames.put("save as", "Shift+Ctrl+S");
                shortcutNames.put("undo", "Ctrl+Z");
                shortcutNames.put("redo", "Ctrl+Y");
            }
        }
        
        return shortcutNames;
    }
}
