package me.giverplay.giveros.core;

import me.giverplay.giveros.impl.WindowImpl;
import me.giverplay.giveros.sdk.gui.Window;

import javax.swing.JFrame;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import static me.giverplay.giveros.messages.I18n.tl;

public final class Desktop extends Canvas
{
  public static final int WIDTH = 1080;
  public static final int HEIGHT = 640;
  public static final int TASKBAR_OFFSET = 42;
  public static final int DECO_OFFSET = 24;
  
  private final JFrame frame;
  private final Graphics graphics;
  private final GiverOS system;
  
  private Window opened;
  private BufferedImage layer;
  
  public Desktop(GiverOS system)
  {
    this.system = system;
    
    setPreferredSize(new Dimension(WIDTH, HEIGHT));
    
    frame = new JFrame();
    frame.setTitle(tl("system.name"));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.add(this);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    
    createBufferStrategy(3);
    graphics = getBufferStrategy().getDrawGraphics();
  
    try
    {
      Images.init();
    }
    catch(Throwable e)
    {
      throw new IllegalStateException(tl("message.error.assets"));
    }
  }
  
  protected void draw()
  {
    clear(0, 0, 0);
    drawBackground();
    drawTaskBar();
    drawWindow();
    getBufferStrategy().show();
  }
  
  protected void drawBackground()
  {
    graphics.drawImage(Images.background, 0, 0, WIDTH, HEIGHT, null);
  }
  
  protected void drawTaskBar()
  {
    graphics.setColor(new Color(90, 89, 89, 210));
    graphics.fillRect(0, HEIGHT - TASKBAR_OFFSET, WIDTH, TASKBAR_OFFSET);
    
    graphics.drawImage(Images.shutdown, 2, HEIGHT - (TASKBAR_OFFSET - 2), TASKBAR_OFFSET - 4, TASKBAR_OFFSET - 4, null);
  }
  
  protected void drawWindow()
  {
    if(opened == null)
      return;
    
    ((WindowImpl) opened).draw(layer.getGraphics());
    graphics.drawImage(layer, 0, DECO_OFFSET, opened.getWidth(), opened.getHeight(), null);
    graphics.setColor(new Color(0x929C9C));
    graphics.fillRect(0, 0, WIDTH, DECO_OFFSET);
    graphics.drawImage(Images.close, WIDTH - DECO_OFFSET, 2, DECO_OFFSET - 4, DECO_OFFSET - 4, null);
    graphics.drawImage(Images.minimize, WIDTH - DECO_OFFSET * 2 - 2, 2, DECO_OFFSET - 4, DECO_OFFSET - 4, null);
  }
  
  protected void clear(int r, int g, int b)
  {
    graphics.setColor(new Color(r, g, b));
    graphics.fillRect(0, 0, WIDTH, HEIGHT);
    
    if(layer != null)
    {
      Graphics gf = layer.getGraphics();
      gf.setColor(new Color(r, g, b));
      gf.fillRect(0, 0, layer.getWidth(), layer.getHeight());
    }
  }
  
  protected void dispose()
  {
    frame.dispose();
  }
  
  public void openWindow(Window window)
  {
    this.opened = window;
    this.layer = new BufferedImage(WIDTH, getWindowHeight(), BufferedImage.TYPE_INT_RGB);
  }
  
  public void closeWindow()
  {
    this.opened = null;
    this.layer = null;
  }
  
  public static int getWindowHeight()
  {
    return HEIGHT - TASKBAR_OFFSET - DECO_OFFSET;
  }
}
