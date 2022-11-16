package fivestage

import chisel3._
import chisel3.util._

class top extends Module{
    val io = IO(new Bundle{
        
        val result_out = Output(SInt(32.W))
    })
    val pc_top = Module(new pC())
    val dataMem_top = Module(new dataMemory())
    val instrMem_top = Module(new instrMem())
    val cntrl_top = Module(new control())
    val immdGen_top = Module(new immdValGen())
    val aluControl_top = Module(new aluControl())
    val alu_top = Module(new ALU())
    val regFile_top = Module(new regFile())
    val jalr_top = Module(new jalr())

    instrMem_top.io.addr := pc_top.io.out(11,2).asUInt
    val instruction = instrMem_top
    pc_top.io.in := pc_top.io.pc4

    cntrl_top.io.op_code := instruction(6,0)
    
    immdGen_top.io.instruction := instruction
    immdGen_top.io.pc_top := pc_top.io.out

    aluControl_top.io.alu_Op := cntrl_top.io.alu_Operation
    aluControl_top.io.func3 := instruction(14,12)
    aluControl_top.io.func7 := instruction(30)

    regFile_top.io.rs1_addr := instruction(19,15)
    regFile_top.io.rs2_addr := instruction(24,20)
    regFile_top.io.rd_addr := instruction(11,7)
    regFile_top.io.writeData := cntrl_top.io.regWrite



    jalr_top.io.rs1 := regFile_top.io.rs1
    jalr_top.io.imm := immdGen_top.io.i_imm

    when(cntrl_top.io.operand_A === "b1".U){
        when(cntrl_top.io.extend_Sel === "b00".U){ //I-Type
            alu_top.io.operand_B := immdGen_top.io.i_imm

        }.elsewhen(cntrl_top.io.extend_Sel === "b01".U){ // S-Type

            alu_top.io.operand_B := immdGen_top.io.s_imm

        }.elsewhen(cntrl_top.io.extend_Sel === "b10".U){ // U-Type

            alu_top.io.operand_B := immdGen_top.io.u_imm


        }.otherwise{
            alu_top.io.operand_B := regFile_top.io.rs2
        }
        
    } 
    
}