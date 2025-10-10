# **Stof**

A custom programming language and compiler implementation

## *Overview*

Stof is a statically-typed, Java-Styled programming language with support for classes, functions, and basic data types. The language also takes some inspiration from C, and Python. This is meant to be a personal side project of mine for fun.

## *Features*

- **Data Types:** `int`, `boolean`, `string`
- **Conditional Statements & Loops:** `if`, `elif`, `else`, `for`, `while`
- **Operators:** Arithmetic (`+`, `-`, `*`, `/`, `%`), Comparison (`>`, `<`, `>=`, `<=`), Increment/Decrement (`++`, `--`)
- **Functions**: First-class functions with parameters and return values
- **Classes:** Object-oriented structure with class declarations

## *Example*
```java
class main {
    int main() {
        int i = 0;
        for (i = 0; i < 10; i++) {
            i++;
        }
        return i;
    }

    boolean isGreater(int a, int b) {
        if (a > b) {
            return true;
        } else {
            return false;
        }
    }
}
```

# **STILL IN PROGRESS**
## *Currently only have the Tokenizer, and the Parse Tree implemented.*


