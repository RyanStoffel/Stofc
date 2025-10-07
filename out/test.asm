global start
section .text
start:
    mov rax, 0x2000001
    mov rdi, 16
    syscall