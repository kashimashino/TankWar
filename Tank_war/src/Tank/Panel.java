package Tank;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

@SuppressWarnings({"all"})

public class Panel extends JPanel implements KeyListener, Runnable {
    //定义我方坦克
    MyTank my_tank = null;
    //定义敌方坦克,放进Vector保存
    Vector<EnemyTank> enemyTanks = new Vector<>();
    //定义爆炸数，存进Vector
    Vector<Boom> booms = new Vector<>();
    int enemyTank_nums = 3;  //敌人的坦克数量

    public Panel() {
        //初始化一个我方坦克
        my_tank = new MyTank(100, 100);
        //初始化敌方坦克
        for (int i = 0; i < enemyTank_nums; i++) {
            EnemyTank enemyTank = new EnemyTank((100 * (i + 1)), 0);
            //设置方向
            enemyTank.setDirection(2);
            //给该enemyTank 加入一颗子弹
            Shot shot = new Shot(enemyTank.getX() + 20, enemyTank.getY() + 60, enemyTank.getDirection());
            //加入到enemyTank的Vector
            enemyTank.shots.add(shot);
            //启动 shot
            new Thread(shot).start();
            //敌方坦克加入进Vector
            enemyTanks.add(enemyTank);
        }
        //定义三张图片，来显示爆炸效果
        //初始化图片对象
        Image  image1 = Toolkit.getDefaultToolkit().getImage(java.awt.Panel.class.getResource("/bomb_1.gif"));
        Image image2 = Toolkit.getDefaultToolkit().getImage(java.awt.Panel.class.getResource("/bomb_2.gif"));
        Image image3 = Toolkit.getDefaultToolkit().getImage(java.awt.Panel.class.getResource("/bomb_3.gif"));
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //判断是否击中敌人
            if (my_tank.shot != null && my_tank.shot.isLive) { //如果子弹还在
                //遍历敌人的坦克,看看有没有坦克被击中
                for (int i = 0; i < enemyTanks.size(); i++) {
                    EnemyTank enemyTank = enemyTanks.get(i);
                    hitEnemy(my_tank.shot, enemyTank);
                }
            }

            //更新窗口，让子弹动起来
            this.repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //按下W/A/S/D按对应方向移动
        if (e.getKeyCode() == KeyEvent.VK_W) { //按下W键
            my_tank.setDirection(0);   //向上
            my_tank.moveUp();
        } else if (e.getKeyCode() == KeyEvent.VK_D) { //按下D键
            my_tank.setDirection(1);   //向右
            my_tank.moveRight();
        } else if (e.getKeyCode() == KeyEvent.VK_S) {//按下S键
            my_tank.setDirection(2);   //向下
            my_tank.moveDown();
        } else if (e.getKeyCode() == KeyEvent.VK_A) {//按下A键
            my_tank.setDirection(3);   //向左
            my_tank.moveLeft();
        }

        //如果用户按下的是J,就发射
        if (e.getKeyCode() == KeyEvent.VK_J) {
            my_tank.ShotEnemy();
        }
        //更新窗口，让坦克动起来
        this.repaint();

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        //以黑色填充矩形
        g.fillRect(0, 0, 1000, 750);

        //调用创建坦克方法生产自己的坦克
        create_tank(my_tank.getX(), my_tank.getY(), g, my_tank.getDirection(), 0);

        //画出我方坦克射击的子弹
        if (my_tank.shot != null && my_tank.shot.isLive == true) {
            g.draw3DRect(my_tank.shot.x, my_tank.shot.y, 5, 5, false);
        }


        //画出敌人的坦克
        for (int i = 0; i < enemyTanks.size(); i++) {
            //Vector中取出坦克
            EnemyTank enemyTank = enemyTanks.get(i);
            //生产敌人坦克
            //如果存在
            if (enemyTank.isLive) {
                create_tank(enemyTank.getX(), enemyTank.getY(), g, enemyTank.getDirection(), 1);
                for (int j = 0; j < enemyTank.shots.size(); j++) {
                    //获取每个敌方坦克的子弹
                    Shot shot = enemyTank.shots.get(j);
                    //画出敌方坦克射击的子弹
                    if (shot.isLive) {
                        g.draw3DRect(shot.x, shot.y, 5, 5, false);
                    } else {
                        //把被鲨了的子弹进程从Vector移除
                        enemyTank.shots.remove(shot);
                    }
                }
            }
        }
    }


    /**
     * tank
     *
     * @param x         横坐标
     * @param y         纵坐标
     * @param g         画笔功能
     * @param direction 坦克移动方向
     * @param type      坦克类型(我方或敌方
     */

    //创建坦克
    public void create_tank(int x, int y, Graphics g, int direction, int type) {
        //两方的坦克颜色
        switch (type) {
            case 0:
                g.setColor(Color.cyan);    //我方坦克颜色，青色
                break;
            case 1:
                g.setColor(Color.yellow);  //敌方坦克颜色，黄色
                break;
        }

        //坦克朝的方向
        switch (direction) {
            case 0: //表示向上
                g.fill3DRect(x, y, 10, 60, false);               //坦克左边轮子
                g.fill3DRect(x + 30, y, 10, 60, false);         //坦克右边轮子
                g.fill3DRect(x + 10, y + 10, 20, 40, false);  //坦克中间矩形部件
                g.fillOval(x + 10, y + 22, 20, 20);                //坦克中间圆形部件
                g.drawLine(x + 20, y + 30, x + 20, y);                       //坦克炮筒
                break;
            case 1: //表示向右
                g.fill3DRect(x, y, 60, 10, false);               //坦克上边轮子
                g.fill3DRect(x, y + 30, 60, 10, false);         //坦克下边轮子
                g.fill3DRect(x + 10, y + 10, 40, 20, false);  //坦克中间矩形部件
                g.fillOval(x + 22, y + 10, 20, 20);                //坦克中间圆形部件
                g.drawLine(x + 30, y + 20, x + 60, y + 20);                 //坦克炮筒
                break;
            case 2: //表示向下
                g.fill3DRect(x, y, 10, 60, false);               //坦克左边轮子
                g.fill3DRect(x + 30, y, 10, 60, false);         //坦克右边轮子
                g.fill3DRect(x + 10, y + 10, 20, 40, false);  //坦克中间矩形部件
                g.fillOval(x + 10, y + 22, 20, 20);                //坦克中间圆形部件
                g.drawLine(x + 20, y + 30, x + 20, y + 60);                 //坦克炮筒
                break;
            case 3: //表示向左
                g.fill3DRect(x, y, 60, 10, false);               //坦克上边轮子
                g.fill3DRect(x, y + 30, 60, 10, false);         //坦克下边轮子
                g.fill3DRect(x + 10, y + 10, 40, 20, false);  //坦克中间矩形部件
                g.fillOval(x + 22, y + 10, 20, 20);                //坦克中间圆形部件
                g.drawLine(x, y + 20, x + 30, y + 20);                       //坦克炮筒
                break;
        }
    }

    //判断我方子弹是否击中敌人
    public void hitEnemy(Shot s, EnemyTank enemyTank) {
        switch (enemyTank.getDirection()) {
            //敌人坦克向上
            case 0:
                if (s.x > enemyTank.getX() && s.x < enemyTank.getX() + 40
                        && s.y > enemyTank.getY() && s.y < enemyTank.getY() + 60) {
                    s.isLive = false;
                    enemyTank.isLive = false;
                    enemyTanks.remove(enemyTank);
                    booms.add(new Boom(enemyTank.getX(),enemyTank.getY()));
                }
                break;
            //向右
            case 1:
                if (s.x > enemyTank.getX() && s.x < enemyTank.getX() + 60
                        && s.y > enemyTank.getY() && s.y < enemyTank.getY() + 40) {
                    s.isLive = false;
                    enemyTank.isLive = false;
                    enemyTanks.remove(enemyTank);
                    booms.add(new Boom(enemyTank.getX(),enemyTank.getY());
                }
                break;
            //向下
            case 2:
                if (s.x > enemyTank.getX() && s.x < enemyTank.getX() + 40
                        && s.y > enemyTank.getY() && s.y < enemyTank.getY() + 60) {
                    s.isLive = false;
                    enemyTank.isLive = false;
                    enemyTanks.remove(enemyTank);
                    booms.add(new Boom(enemyTank.getX(),enemyTank.getY());
                }
                break;
            //向左
            case 3:
                if (s.x > enemyTank.getX() && s.x < enemyTank.getX() + 60
                        && s.y > enemyTank.getY() && s.y < enemyTank.getY() + 40) {
                    s.isLive = false;
                    enemyTank.isLive = false;
                    enemyTanks.remove(enemyTank);
                    booms.add(new Boom(enemyTank.getX(),enemyTank.getY());
                }
                break;
        }
    }



}
