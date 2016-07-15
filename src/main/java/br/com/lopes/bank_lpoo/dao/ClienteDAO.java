/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.lopes.bank_lpoo.dao;

import br.com.lopes.bank_lpoo.beans.Cliente;
import br.com.lopes.bank_lpoo.conexao.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author joaofelipe
 */
public class ClienteDAO {
 
    public Cliente cadastra(Cliente cliente){
        String sql = "INSERT INTO clientes (nome , sobrenome , rg , cpf , endereco_id) VALUES (? , ? , ? , ? , ?)";
        try (Connection con = new ConnectionFactory().getConnection()){
            con.setAutoCommit(false);
            cliente.setEndereco(new EnderecoDAO().cadastra(cliente.getEndereco() , con));
            try (PreparedStatement stmt = con.prepareStatement(sql , Statement.RETURN_GENERATED_KEYS)){
                stmt.setString(1, cliente.getNome());
                stmt.setString(2, cliente.getSobrenome());
                stmt.setString(3, cliente.getRg());
                stmt.setString(4, cliente.getCpf());
                stmt.setLong(5, cliente.getEndereco().getEnderecoId());
                stmt.execute();
                con.commit();
                try (ResultSet rs = stmt.getGeneratedKeys()){
                    if(rs.next()){
                        cliente.setClienteId(rs.getLong(1));
                        return cliente;
                    }else{
                        throw new RuntimeException();
                    }
                } catch (Exception e) {
                    con.rollback();
                    e.printStackTrace();
                    throw new RuntimeException();
                }
            } catch (Exception e) {
                con.rollback();
                e.printStackTrace();
                throw new RuntimeException();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public List<Cliente> lista(){
        String sql = "select cliente_id ,\n" +
                     "       nome ,\n" +
                     "       sobrenome ,\n" +
                     "       rg ,\n" +
                     "       cpf\n" +
                     "from clientes";
        try (Connection con = new ConnectionFactory().getConnection()){
            try (PreparedStatement stmt = con.prepareStatement(sql)){
                try (ResultSet rs = stmt.executeQuery()){
                    List<Cliente> listaClientes = new ArrayList<>();
                    while(rs.next()){
                        Cliente cliente = new Cliente();
                        cliente.setClienteId(rs.getLong("cliente_id"));
                        cliente.setNome(rs.getString("nome"));
                        cliente.setSobrenome(rs.getString("sobrenome"));
                        cliente.setCpf(rs.getString("cpf"));
                        cliente.setRg(rs.getString("rg"));
                        listaClientes.add(cliente);
                    }
                    return listaClientes;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
}
