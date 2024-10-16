START   NOP
        LOAD    10
        STORE   R1
        LOAD    &VAL2
        STORE   R2
        ADD     &REG
        LOAD    5
        STORE   R2
LOOP1   BRZERO  EXIT
        SUB     ONE
        STORE   R2
        BR      LOOP1
EXIT    NOP
HALT    STOP
        END