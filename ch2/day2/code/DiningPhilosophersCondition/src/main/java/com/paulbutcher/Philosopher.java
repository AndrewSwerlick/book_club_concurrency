/***
 * Excerpted from "Seven Concurrency Models in Seven Weeks",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/pb7con for more book information.
***/
package com.paulbutcher;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import java.util.Random;

class Philosopher extends Thread {

  private boolean eating;
  private Philosopher left;
  private Philosopher right;
  private Object table;
  private Random random;
  private int thinkCount;

  public Philosopher(Object table) {
    eating = false;
    this.table = table;
    random = new Random();
  }

  public void setLeft(Philosopher left) { this.left = left; }
  public void setRight(Philosopher right) { this.right = right; }

  public void run() {
    try {
      while (true) {
        think();
        eat();
      }
    } catch (InterruptedException e) {}
  }

  private void think() throws InterruptedException {
    synchronized(table){
      eating = false;
      table.notify();
    }
    ++thinkCount;
    if (thinkCount % 10 == 0)
      System.out.println("Philosopher " + this + " has thought " + thinkCount + " times");
    Thread.sleep(1000);
  }

  private void eat() throws InterruptedException {
    synchronized(table) {
      while (left.eating || right.eating)
        table.wait();
      eating = true;

      if (left.eating || right.eating)
        System.out.println("Philosopher " + this + " is eating with his hands");
    }
    Thread.sleep(1000);
  }
}
