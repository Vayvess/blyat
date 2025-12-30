#set page(
  width: 210mm,
  height: 297mm,
  margin: (top: 25mm, bottom: 25mm, left: 30mm, right: 30mm),
)

#set text(
  font: "Droid Sans Fallback",
  size: 16pt,
)

#set heading(numbering: "1.1")

#set par(justify: true)
#set list(indent: 1.2em)


// --------------------------------------------------
// Title Page
// --------------------------------------------------

#align(center)[
  #v(128pt)

  #text(size: 28pt, weight: "bold")[
    Blyat: Absolute Cinema
  ]

  #v(12pt)

  #text(size: 14pt, style: "italic")[
    A Programming Language in Motion
  ]

  #v(40pt)

  #text(size: 11pt)[
    Design notes, guide, and evolving specification
  ]

  #v(10pt)

  #text(size: 10pt, fill: gray)[
    Version 0.1 — Work in Progress
  ]
]

#pagebreak()


= Introduction

Blyat is a programming language built around message-passing and the actor model.

*Blyat* stands for *'Better Language Yet Another Transpiler'*



// --------------------------------------------------
// What Blyat Is
// --------------------------------------------------

== What Blyat Is

*Blyat* stands for *'Better Language Yet Another Transpiler'*


// --------------------------------------------------
// Core Concepts (Preview)
// --------------------------------------------------

#pagebreak()
= Core Concepts

This section introduces the main ideas and their semantics.



== Declare an Actor

An *actor* is an isolated unit of computation.
It owns its state and reacts to incoming messages.

```blyat
actor Player
{
    stage {
        Int armor;
        Int health;
        Int maxHealth;
    }

    upon heal(n) {
        health = max(health + n, maxHealth);
    }
}

actor Scene
{

}
```

== Spawning an actor

```blyat
player = spawn Player::init(2, 10, 10);
```

== 



= Behind the cameras


// --------------------------------------------------
// Example (Early Sketch)
// --------------------------------------------------

#pagebreak()
= First Sketch

```blyat
actor Account
{
    stage {
        Int balance;
    }

    mold init(balance) {
        this.balance = balance;
    }

    upon deposit(n) {
        this.balance += n;
    }
}

func main(Array args) {
    bank = spawn Bank::init();


}

```
