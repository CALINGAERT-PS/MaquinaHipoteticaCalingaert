START   NOP                 
        LOAD    10     
        STORE   R1        
        LOAD    20           
        STORE   R2         
        ADD     R1             
        LOAD    5        
        STORE   R2        
LOOP1   BRZERO  EXIT       
        SUB     ONE        
        STORE   R2        
        BR      LOOP1       
EXIT    NOP                 
HALT    STOP
R1	SPACE
R2	SPACE
ONE	CONST	1     
        END      