package com.mm.marketgauge.util

object FpInScalaErrorHandling {

  sealed trait Option[+A] {

    def map[B](f: A => B): Option[B] = this match {
      // apply f if the option is not None
      case Some(a) => Some(f(a))
      case None    => None
    }

    def getOrElse[B >: A](default: => B): B = this match {
      case Some(value) => value
      case None        => default
    }

    def flatMap[B](f: A => Option[B]): Option[B] = this match {
      // apply f, which may fail , to the option if not None 
      case None       => None
      case Some(item) => f(item)

      // map(f).getOrElse(None)

    }

    def orElse[B >: A](ob: => Option[B]): Option[B] = this match {
      // don't evaluate ob unless needed
      case None    => ob
      case Some(x) => this

    }

    def filter(f: A => Boolean): Option[A] = this match {
      // Convert Some to None if the value does not satisfy f
      case Some(item) if f(item) => this
      case None                  => None

      //this.flatMap { item => if (f(item)) Some(item) else None } 
    }

    // Reimplement the functions reusing others
    def flatMap_1[B](f: A => Option[B]): Option[B] = {
      map(f).getOrElse(None)
    }

    def orElse_1[B >: A](ob: => Option[B]): Option[B] = {
      // returns the first Option if it's defind else the second option
      this.map { item => Some(item) }.getOrElse(ob)
    }

    def filter_1(f: A => Boolean): Option[A] = {
      // if predicate true return this else None
      this.flatMap { x => if (f(x)) Some(x) else None }
    }

    def lift[A, B](f: A => B): Option[A] => Option[B] = _ map f

    def mean(xs: Seq[Double]): Option[Double] = {
      if (xs.isEmpty) None
      else Some(xs.sum / xs.size)
    }
    import math._

    def map2[A, B, C](a: Option[A], b: Option[B])(f: (A, B) => C): Option[C] = {
      a flatMap (aa => b map (bb => f(aa, bb)))

    }

  }

  case class Some[+A](get: A) extends Option[A]
  case object None extends Option[Nothing]

}