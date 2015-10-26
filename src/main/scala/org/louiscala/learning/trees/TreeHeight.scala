package org.louiscala.learning.trees

object TreeHeight extends App {

  trait Node
  case class  InnerTreeNode ( left:Node, right:Node, value:Int ) extends Node
  case object StopNode extends Node

  def leaf(x: Int): Node = InnerTreeNode(StopNode, StopNode, x)
  def node(x: Int)(left: Node, right: Node) = InnerTreeNode(left, right, x)

  val tree = node(0)(
     leaf(1),
     node(4)(
        leaf(2),
        leaf(3)
    )
  )


  // val tree = InnerTreeNode ( leaf(1), InnerTreeNode(StopNode, leaf(8), 5) ,3 )
  // val tree = leaf(1)

  def height(node: Node): Int = {
    def numberOfNodesOnLongestPath(node:Node, current: Int = 0) : Int = node match {
      case StopNode => current
      case InnerTreeNode( left, right, _ ) =>
        val leftHeight = numberOfNodesOnLongestPath(left, current + 1)
        val rightHeight = numberOfNodesOnLongestPath(right, current + 1)
        leftHeight max rightHeight
    }
    numberOfNodesOnLongestPath(node) - 1
  }

  println(height(tree))

  val emptyTree = StopNode
  println(height(emptyTree))

}
