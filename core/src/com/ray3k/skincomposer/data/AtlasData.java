/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2018 Raymond Buckley
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
package com.ray3k.skincomposer.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.ray3k.skincomposer.Main;
import com.ray3k.skincomposer.data.DrawableData.DrawableType;
import com.ray3k.skincomposer.utils.Utils;
import com.ray3k.tenpatch.TenPatchDrawable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

public class AtlasData implements Json.Serializable {
    public boolean atlasCurrent = false;
    private Array<DrawableData> drawables;
    private Array<DrawableData> fontDrawables;
    private Main main;
    private TextureAtlas atlas;
    public ObjectMap<DrawableData, Drawable> drawablePairs;
    
    public AtlasData() {
        drawables = new Array<>();
        fontDrawables = new Array<>();
        drawablePairs = new ObjectMap<>();
    }

    public void setMain(Main main) {
        this.main = main;
    }
    
    public void clear() {
        drawables.clear();
        fontDrawables.clear();
        atlasCurrent = false;
    }

    public Array<DrawableData> getDrawables() {
        return drawables;
    }
    
    public DrawableData getDrawable(String name) {
        DrawableData returnValue = null;
        for (DrawableData data : drawables) {
            if (data.name.equals(name)) {
                returnValue = data;
                break;
            }
        }
        
        return returnValue;
    }

    public Array<DrawableData> getFontDrawables() {
        return fontDrawables;
    }
    
    public DrawableData getFontDrawable(String name) {
        DrawableData returnValue = null;
        for (DrawableData data : fontDrawables) {
            if (data.name.equals(name)) {
                returnValue = data;
                break;
            }
        }
        
        return returnValue;
    }
    
    public void readAtlas(FileHandle fileHandle) throws Exception {
        if (fileHandle.exists()) {
            FileHandle saveFile = main.getProjectData().getSaveFile();
            FileHandle targetDirectory;
            if (saveFile != null) {
                targetDirectory = saveFile.sibling(saveFile.nameWithoutExtension() + "_data/");
            } else {
                targetDirectory = Main.appFolder.child("temp/" + main.getProjectData().getId() + "_data/");
            }
            
            targetDirectory.mkdirs();
            
            TextureAtlas atlas = new TextureAtlas(fileHandle);
            Array<AtlasRegion> regions = atlas.getRegions();
            
            for (AtlasRegion region : regions) {
                Texture texture = region.getTexture();
                if (!texture.getTextureData().isPrepared()) {
                    texture.getTextureData().prepare();
                }
                
                Pixmap pixmap = texture.getTextureData().consumePixmap();
                pixmap.setBlending(Pixmap.Blending.None);
                Pixmap savePixmap;
                String name;
                
                if (region.splits == null && region.pads == null) {
                    name = region.name + ".png";
                    savePixmap = new Pixmap(region.getRegionWidth(), region.getRegionHeight(), Pixmap.Format.RGBA8888);
                    savePixmap.setBlending(Pixmap.Blending.None);
                    for (int x = 0; x < region.getRegionWidth(); x++) {
                        for (int y = 0; y < region.getRegionHeight(); y++) {
                            int colorInt = pixmap.getPixel(region.getRegionX() + x, region.getRegionY() + y);
                            savePixmap.drawPixel(x, y, colorInt);
                        }
                    }
                } else {
                    name = region.name + ".9.png";
                    savePixmap = new Pixmap(region.getRegionWidth() + 2, region.getRegionHeight() + 2, pixmap.getFormat());
                    savePixmap.setBlending(Pixmap.Blending.None);
                    int x;
                    int y;
                    
                    //draw 9 patch lines
                    savePixmap.setColor(Color.BLACK);

                    if (region.splits != null) {
                        x = 0;
                        for (y = region.splits[2] + 1; y < savePixmap.getHeight() - region.splits[3] - 1; y++) {
                            savePixmap.drawPixel(x, y);
                        }
                        
                        y = 0;
                        for (x = region.splits[0] + 1; x < savePixmap.getWidth() - region.splits[1] - 1; x++) {
                            savePixmap.drawPixel(x, y);
                        }
                    }
                    
                    if (region.pads != null) {
                        x = savePixmap.getWidth() - 1;
                        for (y = region.pads[2] + 1; y < savePixmap.getHeight() - region.pads[3] - 1; y++) {
                            savePixmap.drawPixel(x, y);
                        }
                        
                        y = savePixmap.getHeight() - 1;
                        for (x = region.pads[0] + 1; x < savePixmap.getWidth() - region.pads[1] - 1; x++) {
                            savePixmap.drawPixel(x, y);
                        }
                    }

                    for (x = 0; x < region.getRegionWidth(); x++) {
                        for (y = 0; y < region.getRegionHeight(); y++) {
                            int colorInt = pixmap.getPixel(region.getRegionX() + x, region.getRegionY() + y);
                            savePixmap.drawPixel(x + 1, y + 1, colorInt);
                        }
                    }
                }
                FileHandle outputFile = targetDirectory.child(name);
                PixmapIO.writePNG(outputFile, savePixmap);
                DrawableData drawable = new DrawableData(outputFile);
                if (Utils.isNinePatch(outputFile.name())) {
                    drawable.type = DrawableType.NINE_PATCH;
                } else {
                    drawable.type = DrawableType.TEXTURE;
                }
                
                //delete drawables with the same name
                for (DrawableData originalData : new Array<>(main.getProjectData().getAtlasData().getDrawables())) {
                    if (originalData.name.equals(drawable.name)) {
                        main.getProjectData().getAtlasData().getDrawables().removeValue(originalData, true);
                    }
                }
                    
                drawables.add(drawable);
            }
            
            
        } else {
            throw new FileNotFoundException();
        }
    }
    
    public Array<String> writeAtlas(FileHandle settingsFile) throws Exception {
        return writeAtlas(Main.appFolder.child("temp/" + main.getProjectData().getId() + ".atlas"), settingsFile);
    }
    
    public Array<String> writeAtlas(FileHandle targetFile, FileHandle settingsFile) throws Exception {
        Array<String> warnings = new Array<>();
        targetFile.parent().mkdirs();
        FileHandle[] oldFiles = targetFile.parent().list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String string) {
                return string.matches(targetFile.nameWithoutExtension() + "\\d*\\.(?i)png");
            }
        });
        for (FileHandle fileHandle : oldFiles) {
            fileHandle.delete();
        }
        targetFile.sibling(targetFile.nameWithoutExtension() + ".atlas").delete();
        
        Array<FileHandle> files = new Array<>();
        
        
        for (DrawableData drawable : fontDrawables) {
            if (!files.contains(drawable.file, false)) {
                files.add(drawable.file);
            }
            
            if (!main.getProjectData().resourceExists(drawable.file)) {
                warnings.add("[RED]ERROR:[] Drawable file [BLACK]" + drawable.file + "[] does not exist.");
            }
        }
        
        for (DrawableData drawable : drawables) {
            if (!drawable.customized) {
                if (!files.contains(drawable.file, false)) {
                    files.add(drawable.file);
                }

                if (!main.getProjectData().resourceExists(drawable.file)) {
                    warnings.add("[RED]ERROR:[] Drawable file [BLACK]" + drawable.file + "[] does not exist.");
                }
            }
        }
        
        main.getDesktopWorker().texturePack(files, main.getProjectData().getSaveFile(), targetFile, settingsFile);
        return warnings;
    }
    
    public TextureAtlas getAtlas() {
        TextureAtlas atlas = null;
        FileHandle atlasFile = Main.appFolder.child("temp/" + main.getProjectData().getId() + ".atlas");
        if (atlasFile.exists()) {
            atlas = new TextureAtlas(atlasFile);
        }
        return atlas;
    }
    
    public void clearTempData() {
        FileHandle tempFolder = Main.appFolder.child("temp/");
        tempFolder.deleteDirectory();
    }
    
    public void set(AtlasData atlasData) {
        drawables.clear();
        drawables.addAll(atlasData.drawables);
        
        fontDrawables.clear();
        fontDrawables.addAll(atlasData.fontDrawables);
    }

    @Override
    public void write(Json json) {
        json.writeValue("atlasCurrent", atlasCurrent);
        json.writeValue("drawables", drawables, Array.class, DrawableData.class);
        json.writeValue("fontDrawables", fontDrawables, Array.class, DrawableData.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        atlasCurrent = json.readValue("atlasCurrent", Boolean.TYPE, jsonData);
        drawables = json.readValue("drawables", Array.class, DrawableData.class, jsonData);
        fontDrawables = json.readValue("fontDrawables", Array.class, DrawableData.class, new Array<DrawableData>(),jsonData);
        assignDrawableTypes();
    }
    
    private void assignDrawableTypes() {
        for (DrawableData drawable : drawables) {
            if (drawable.type == null) {
                if (drawable.tiled) {
                    drawable.type = DrawableType.TILED;
                } else if (drawable.customized) {
                    drawable.type = DrawableType.CUSTOM;
                } else if (drawable.tenPatchData != null) {
                    drawable.type = DrawableType.TENPATCH;
                } else if (drawable.tintName != null) {
                    drawable.type = DrawableType.TINTED_FROM_COLOR_DATA;
                } else if (drawable.tint != null) {
                    drawable.type = DrawableType.TINTED;
                } else if (drawable.file != null) {
                    if (Utils.isNinePatch(drawable.file.name())) {
                        drawable.type = DrawableType.NINE_PATCH;
                    } else {
                        drawable.type = DrawableType.TEXTURE;
                    }
                }
            }
        }
        
        for (DrawableData drawable : fontDrawables) {
            drawable.type = DrawableType.FONT;
        }
    }
    
    public boolean checkIfNameExists(String name) {
        return checkIfDrawableNameExists(name) || checkIfFontDrawableNameExists(name);
    }
    
    /**
     * Returns true if any existing drawable has the indicated name.
     * @param name
     * @return
     */
    public boolean checkIfDrawableNameExists(String name) {
        boolean returnValue = false;
        
        for (DrawableData drawable : getDrawables()) {
            if (drawable.name.equals(name)) {
                returnValue = true;
                break;
            }
        }
        
        return returnValue;
    }
    
    /**
     * Returns true if any existing drawable has the indicated name.
     * @param name
     * @return
     */
    public boolean checkIfFontDrawableNameExists(String name) {
        boolean returnValue = false;
        
        for (DrawableData drawable : getFontDrawables()) {
            if (drawable.name.equals(name)) {
                returnValue = true;
                break;
            }
        }
        
        return returnValue;
    }
    
    /**
     * Writes a TextureAtlas based on drawables list. Creates drawables to be
     * displayed on screen
     * @return
     */
    public boolean produceAtlas() {
        try {
            drawablePairs.clear();
            
            if (!main.getAtlasData().atlasCurrent) {
                if (atlas != null) {
                    atlas.dispose();
                    atlas = null;
                }
                FileHandle defaultsFile = Main.appFolder.child("texturepacker/atlas-internal-settings.json");
                main.getAtlasData().writeAtlas(defaultsFile);
                main.getAtlasData().atlasCurrent = true;
                
                //clear all regions in any tenPatchData
                for (var data : main.getAtlasData().getDrawables()) {
                    if (data.tenPatchData != null) {
                        data.tenPatchData.regions = null;
                    }
                }
            }
            atlas = main.getAtlasData().getAtlas();
            
            var combined = new Array<>(getDrawables());
            combined.addAll(getFontDrawables());
            
            for (DrawableData data : combined) {
                Drawable drawable;
                if (data.customized) {
                    drawable = main.getSkin().getDrawable("custom-drawable-skincomposer-image");
                } else if (data.tenPatchData != null) {
                    var region = atlas.findRegion(DrawableData.proper(data.file.name()));
                    drawable = new TenPatchDrawable(data.tenPatchData.horizontalStretchAreas.toArray(),
                            data.tenPatchData.verticalStretchAreas.toArray(), data.tenPatchData.tile, region);
                    if (((TenPatchDrawable) drawable).horizontalStretchAreas.length == 0) {
                        ((TenPatchDrawable) drawable).horizontalStretchAreas = new int[] {0, region.getRegionWidth() - 1};
                    }
                    if (((TenPatchDrawable) drawable).verticalStretchAreas.length == 0) {
                        ((TenPatchDrawable) drawable).verticalStretchAreas = new int[] {0, region.getRegionHeight() - 1};
                    }
                    
                    drawable.setLeftWidth(data.tenPatchData.contentLeft);
                    drawable.setRightWidth(data.tenPatchData.contentRight);
                    drawable.setTopHeight(data.tenPatchData.contentTop);
                    drawable.setBottomHeight(data.tenPatchData.contentBottom);
                    
                    if (!MathUtils.isEqual(data.minWidth, -1)) drawable.setMinWidth(data.minWidth);
                    if (!MathUtils.isEqual(data.minHeight, -1)) drawable.setMinHeight(data.minHeight);
                    if (data.tenPatchData.colorName != null) ((TenPatchDrawable) drawable).setColor(main.getJsonData().getColorByName(data.tenPatchData.colorName).color);
                    if (data.tenPatchData.color1Name != null) ((TenPatchDrawable) drawable).setColor1(main.getJsonData().getColorByName(data.tenPatchData.color1Name).color);
                    if (data.tenPatchData.color2Name != null) ((TenPatchDrawable) drawable).setColor2(main.getJsonData().getColorByName(data.tenPatchData.color2Name).color);
                    if (data.tenPatchData.color3Name != null) ((TenPatchDrawable) drawable).setColor3(main.getJsonData().getColorByName(data.tenPatchData.color3Name).color);
                    if (data.tenPatchData.color4Name != null) ((TenPatchDrawable) drawable).setColor4(main.getJsonData().getColorByName(data.tenPatchData.color4Name).color);
                    ((TenPatchDrawable) drawable).setOffsetX(data.tenPatchData.offsetX);
                    ((TenPatchDrawable) drawable).setOffsetY(data.tenPatchData.offsetY);
                    ((TenPatchDrawable) drawable).setOffsetXspeed(data.tenPatchData.offsetXspeed);
                    ((TenPatchDrawable) drawable).setOffsetYspeed(data.tenPatchData.offsetYspeed);
                    ((TenPatchDrawable) drawable).setFrameDuration(data.tenPatchData.frameDuration);
                    ((TenPatchDrawable) drawable).setPlayMode(data.tenPatchData.playMode);
                    if (data.tenPatchData.regions == null) {
                        data.tenPatchData.regions = new Array<>();
                        for (var name : data.tenPatchData.regionNames) {
                            data.tenPatchData.regions.add(atlas.findRegion(name));
                        }
                    }
                    ((TenPatchDrawable) drawable).setRegions(data.tenPatchData.regions);
                } else if (data.tiled) {
                    String name = data.file.name();
                    name = DrawableData.proper(name);
                    drawable = new TiledDrawable(atlas.findRegion(name));
                    drawable.setMinWidth(data.minWidth);
                    drawable.setMinHeight(data.minHeight);
                    ((TiledDrawable) drawable).getColor().set(main.getJsonData().getColorByName(data.tintName).color);
                } else if (Utils.isNinePatch(data.file.name())) {
                    String name = data.file.name();
                    name = DrawableData.proper(name);
                    drawable = new NinePatchDrawable(atlas.createPatch(name));
                    if (data.tint != null) {
                        drawable = ((NinePatchDrawable) drawable).tint(data.tint);
                    } else if (data.tintName != null) {
                        drawable = ((NinePatchDrawable) drawable).tint(main.getJsonData().getColorByName(data.tintName).color);
                    }
                    if (!MathUtils.isEqual(data.minWidth, -1)) drawable.setMinWidth(data.minWidth);
                    if (!MathUtils.isEqual(data.minHeight, -1)) drawable.setMinHeight(data.minHeight);
                } else {
                    String name = data.file.name();
                    name = DrawableData.proper(name);
                    drawable = new SpriteDrawable(atlas.createSprite(name));
                    if (data.tint != null) {
                        drawable = ((SpriteDrawable) drawable).tint(data.tint);
                    } else if (data.tintName != null) {
                        drawable = ((SpriteDrawable) drawable).tint(main.getJsonData().getColorByName(data.tintName).color);
                    }
                    if (!MathUtils.isEqual(data.minWidth, -1)) drawable.setMinWidth(data.minWidth);
                    if (!MathUtils.isEqual(data.minHeight, -1)) drawable.setMinHeight(data.minHeight);
                }
                
                drawablePairs.put(data, drawable);
            }
            return true;
        } catch (Exception e) {
            Gdx.app.error(getClass().getName(), "Error while attempting to generate drawables.", e);
            main.getDialogFactory().showDialogError("Atlas Error...","Error while attempting to generate drawables.\n\nOpen log?");
            return false;
        }
    }
    
    public ObjectMap<DrawableData, Drawable> getDrawablePairs() {
        return drawablePairs;
    }
}