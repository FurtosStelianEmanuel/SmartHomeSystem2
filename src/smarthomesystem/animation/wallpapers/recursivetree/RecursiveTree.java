/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smarthomesystem.animation.wallpapers.recursivetree;

import bananaconvert.marshaler.exception.DeserializationException;
import bananaconvert.marshaler.exception.SerializationException;
import data.Serializer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.util.Pair;
import javax.swing.SwingUtilities;
import misc.Misc;
import smarthomesystem.animation.wallpapers.Layer;
import smarthomesystem.animation.wallpapers.Wallpaper;
import static smarthomesystem.SmartHomeSystem.container;
import java.io.IOException;
import java.util.Arrays;
import messaging.MessageBroker;
import messaging.MessageFactory;
import messaging.exceptions.PackingNotImplementedException;
import smarthomesystem.ledstrips.RgbStripDetailProjection;
import smarthomesystem.repos.RgbStripRepository;
import smarthomesystem.animation.AnimationConfig;
import smarthomesystem.RgbColorSender;
import smarthomesystem.commands.SetColorSmoothlyCommand;

/**
 *
 * @author Manel
 */
public class RecursiveTree extends Wallpaper implements RgbColorSender, AnimationConfig {

    private boolean isAdjustingAngle = false;
    private final List<Branch> tree;
    private final List<Leaf> leaves;
    private double branchAngle = Math.PI / 4;
    private double branchDescendantsLength = .7d;
    private int maximumBranchIterations = 5;
    private double leafDiameter = 15;
    private boolean showTree = true;
    private final Map<Integer, Color> leavesColors;
    private Color previousColor = Color.black;

    private final Serializer shsSerializer;
    private final Path serializationPath;
    private final MessageFactory messageFactory;
    private final RgbStripRepository rgbStripRepository;

    public RecursiveTree() {
        tree = new ArrayList<>();
        leaves = new ArrayList<>();
        leavesColors = new HashMap<>();
        shsSerializer = container != null ? container.resolveDependencies(Serializer.class) : null;
        serializationPath = shsSerializer != null ? Paths.get(shsSerializer.getSerializationPath().toString(), "recursiveTree.json") : null;
        messageFactory = container != null ? container.resolveDependencies(MessageFactory.class) : null;
        rgbStripRepository = container != null ? container.resolveDependencies(RgbStripRepository.class) : null;

        setOpaque(false);
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mwe) {
                if (mwe.getWheelRotation() < 0) {
                    if (isAdjustingAngle) {
                        maximumBranchIterations++;
                    } else {
                        leafDiameter++;
                    }
                } else {
                    if (isAdjustingAngle) {
                        maximumBranchIterations--;
                    } else {
                        leafDiameter--;
                    }
                }

                recalculateBranches();
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me)) {
                    changeBranchAngle(me);
                } else if (SwingUtilities.isLeftMouseButton(me)) {
                    sendDraggedLeafColor(me);
                }
            }

            @Override
            public void mouseMoved(MouseEvent me) {
                boolean leafHovered = checkLeavesForMouseInteraction(me);

                if (leafHovered) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }

            private void sendDraggedLeafColor(MouseEvent me) {
                if (!SwingUtilities.isLeftMouseButton(me)) {
                    return;
                }

                checkLeavesForMouseInteraction(me);

                List<Leaf> hoveredLeaves = leaves.stream().filter(l -> l.isHovered() && l.isColored()).collect(Collectors.toList());

                if (hoveredLeaves.isEmpty() || hoveredLeaves.size() > 1) {
                    return;
                }

                Leaf hoveredLeaf = hoveredLeaves.stream().findFirst().orElse(null);
                if (!hoveredLeaf.isColored()) {
                    return;
                }

                if (!hoveredLeaf.isTouched()) {
                    hoveredLeaf.touch();
                    sendColor(hoveredLeaf.getColor());
                }
            }

            private boolean checkLeavesForMouseInteraction(MouseEvent me) {
                boolean leafHovered = false;

                for (Leaf leaf : leaves) {
                    leaf.checkIfHovered(me);

                    if (leaf.isHovered()) {
                        leafHovered = true;
                    }
                }

                return leafHovered;
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me)) {
                    changeBranchAngle(me);
                }
            }

            @Override
            public void mouseClicked(MouseEvent me) {
                checkLeavesForMouseClick(me);
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                if (SwingUtilities.isRightMouseButton(me)) {
                    isAdjustingAngle = false;
                    repaint();
                }
            }

            private void checkLeavesForMouseClick(MouseEvent me) {
                if (!SwingUtilities.isLeftMouseButton(me)) {
                    return;
                }

                List<Leaf> hoveredLeaves = leaves.stream().filter(l -> l.isHovered()).collect(Collectors.toList());
                if (hoveredLeaves.isEmpty() || hoveredLeaves.size() > 1) {
                    return;
                }

                Leaf hoveredLeaf = hoveredLeaves.stream().findFirst().orElse(null);

                if (hoveredLeaf.isColored()) {
                    sendColor(hoveredLeaf.getColor());
                    return;
                }

                Color choosenColor = Misc.chooseColor(RecursiveTree.this, "Color leaf", null);
                if (choosenColor == null) {
                    return;
                }

                leavesColors.put(leaves.indexOf(hoveredLeaf), choosenColor);
                hoveredLeaf.setColor(choosenColor);
                repaint();
            }
        });
    }

    Layer[] layers = new Layer[]{
        new Layer() {
            @Override
            public void show() {
                painter.setColor(new Color(0, 0, 0, 0));
                painter.fillRect(0, 0, getWidth(), getHeight());
            }

            @Override
            public boolean shouldShow() {
                return true;
            }
        },
        new Layer() {
            Stroke stroke = new BasicStroke(2f);

            @Override
            public boolean shouldShow() {
                return showTree;
            }

            @Override
            public void show() {
                painter.setColor(Color.white);

                for (Branch branch : tree) {
                    painter.draw(branch.getShape());
                }
            }
        },
        new Layer() {
            @Override
            public void show() {
                for (Leaf leaf : leaves) {
                    Shape leafShape = leaf.getShape();

                    if (!leaf.isColored()) {
                        continue;
                    }

                    painter.setColor(leaf.getColor());
                    painter.fill(leafShape);
                    painter.draw(leafShape);
                }
            }

            @Override
            public boolean shouldShow() {
                return true;
            }
        },
        new Layer() {
            Stroke stroke = new BasicStroke(5f);

            @Override
            public void show() {
                painter.setColor(Color.white);
                painter.setStroke(stroke);
                painter.drawRect(0, 0, getWidth(), getHeight());
            }

            @Override
            public boolean shouldShow() {
                return isAdjustingAngle;
            }
        }
    };

    @Override
    protected void paintWallpaper() {
        paintLayers(layers);
    }

    @Override
    protected void onSizeSet() {
        setPreviousTreeConfig();
        recalculateBranches();
        repaint();
    }

    @Override
    public void sendColor(Color color) {
        MessageBroker messageBroker = container.resolveDependencies(MessageBroker.class);
        SetColorSmoothlyCommand setColorSmoothlyCommand = messageFactory.createReflectiveInstance(SetColorSmoothlyCommand.class);

        RgbStripDetailProjection[] strips = rgbStripRepository.getStrips();
        RgbStripDetailProjection primaryStrip = Arrays.asList(strips).stream().filter(s -> s.isPrimary).findFirst().orElse(null);

        setColorSmoothlyCommand.stripType = 0;
        setColorSmoothlyCommand.redPin = primaryStrip.redPin;
        setColorSmoothlyCommand.greenPin = primaryStrip.greenPin;
        setColorSmoothlyCommand.bluePin = primaryStrip.bluePin;

        setColorSmoothlyCommand.currentRed = getPreviousColor().getRed();
        setColorSmoothlyCommand.currentGreen = getPreviousColor().getGreen();
        setColorSmoothlyCommand.currentBlue = getPreviousColor().getBlue();

        setColorSmoothlyCommand.increment = 3;
        setColorSmoothlyCommand.targetRed = color.getRed();
        setColorSmoothlyCommand.targetGreen = color.getGreen();
        setColorSmoothlyCommand.targetBlue = color.getBlue();

        previousColor = color;
        
        try {
            messageBroker.send(setColorSmoothlyCommand);
        } catch (IOException | PackingNotImplementedException ex) {
            Logger.getLogger(RecursiveTree.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Color getPreviousColor() {
        return previousColor;
    }

    @Override
    public Object mapToSerializedFormat() {
        return new RecursiveTreeSerializedFormat() {
            {
                nrOfIterations = maximumBranchIterations;
                leaves = RecursiveTree.this.leaves.stream().map(l
                        -> new SerializedLeaf(RecursiveTree.this.leaves.indexOf(l), l)
                ).collect(Collectors.toList());
                angle = branchAngle;
                leafDiameter = RecursiveTree.this.leafDiameter;
                showTree = RecursiveTree.this.showTree;
            }
        };
    }

    @Override
    public void save() {
        try {
            shsSerializer.serializeAsJson(shsSerializer.getBananaConvert().serializeToJson(mapToSerializedFormat()), serializationPath);
        } catch (FileNotFoundException | SerializationException ex) {
            Logger.getLogger(RecursiveTree.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setPreviousTreeConfig() {
        if (shsSerializer == null) {
            return;
        }

        try {
            leavesColors.clear();

            RecursiveTreeSerializedFormat deserializedTree = shsSerializer.getBananaConvert().deserializeJson(serializationPath, RecursiveTreeSerializedFormat.class);
            maximumBranchIterations = deserializedTree.nrOfIterations;
            branchAngle = deserializedTree.angle;
            leafDiameter = deserializedTree.leafDiameter;
            showTree = deserializedTree.showTree;

            List<SerializedLeaf> coloredLeaves = deserializedTree.leaves.stream().filter(l -> l.isColored).collect(Collectors.toList());
            for (SerializedLeaf leaf : coloredLeaves) {
                leavesColors.put(leaf.index, new Color(leaf.red, leaf.green, leaf.blue, leaf.alpha));
            }

        } catch (DeserializationException ex) {
            Logger.getLogger(RecursiveTree.class.getName()).log(Level.INFO, null, ex);
        }
    }

    private void changeBranchAngle(MouseEvent me) {
        isAdjustingAngle = true;
        branchAngle = Misc.map(me.getX(), 0, getWidth(), 0, 2 * Math.PI);
        recalculateBranches();
        repaint();
    }

    private void recalculateBranches() {
        tree.clear();
        leaves.clear();

        tree.add(new Branch(getWidth() / 2, getHeight(), getWidth() / 2, getHeight() - getHeight() * 0.3));

        for (int i = 0; i < maximumBranchIterations; i++) {
            growTree();
        }

        List<Branch> youngBranches = tree.stream().filter(b -> !b.isGrown()).collect(Collectors.toList());
        for (Branch branch : youngBranches) {
            Leaf leaf = branch.growLeaf(leafDiameter);
            leaves.add(leaf);

            if (leavesColors.containsKey(leaves.size() - 1)) {
                leaf.setColor(leavesColors.get(leaves.size() - 1));
            }
        }
    }

    private void growTree() {
        for (int i = tree.size() - 1; i >= 0; i--) {
            Branch b = tree.get(i);
            if (b.isGrown()) {
                continue;
            }

            Pair<Branch, Branch> grownBranches = b.grow(branchAngle, branchDescendantsLength);
            tree.add(grownBranches.getKey());
            tree.add(grownBranches.getValue());
        }
    }
}
