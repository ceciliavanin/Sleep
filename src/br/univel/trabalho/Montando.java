package br.univel.trabalho;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import br.univel.enums.EstadoCivil;
import br.univel.trabalho.Cliente;
import br.univel.annotations.Coluna;
import br.univel.annotations.Tabela;
import br.univel.annotations.*;

public class Montando extends SQLGen {
		
		public Montando () {
			String strCreateTable = getCreateTable(Cliente.class);
			System.out.println(strCreateTable);
			String strDropTable = getDropTable(Cliente.class);
			System.out.println(strDropTable);
			Cliente cliente = new Cliente(1, "Cecilia", "Rua Souza Naves", "45888888888", EstadoCivil.SOLTEIRO);
			Connection con = null;

			try {
				con = new Conex();
				PreparedStatement ps = getSqlInsert(con, cliente);
				ps.executeUpdate();
				ps = getSqlSelectAll(con, cliente);
				ps = getSqlSelectById(con, cliente);

				ps.close();
				con.close();

			} catch (SQLException e) {
				e.printStackTrace();

			}
			
		}

		@Override
		protected String getCreateTable(Class<Cliente> cl) {
			try {

				StringBuilder sb = new StringBuilder();
				
				{
					String nomeTabela;
					if (cl.isAnnotationPresent(Tabela.class)) {

						Tabela anotacaoTabela = cl.getAnnotation(Tabela.class);
						nomeTabela = anotacaoTabela.value();

					} else {
						nomeTabela = cl.getSimpleName().toUpperCase();

					}
					sb.append("CREATE TABLE ").append(nomeTabela).append(" (");
				}

				Field[] atributos = cl.getDeclaredFields();

				{
					for (int i = 0; i < atributos.length; i++) {

						Field field = atributos[i];

						String nomeColuna;
						String tipoColuna = null;
						int tamanhoColuna;
						Coluna anotacaoColuna = null;
						if (field.isAnnotationPresent(Coluna.class)) {
							anotacaoColuna = field.getAnnotation(Coluna.class);

							if (anotacaoColuna.nome().isEmpty()) {
								nomeColuna = field.getName().toUpperCase();
							} else {
								nomeColuna = anotacaoColuna.nome();
								
							}

						} else {
							nomeColuna = field.getName().toUpperCase();
						}

						Class<?> tipoParametro = field.getType();

						if (tipoParametro.equals(String.class)) {
								tamanhoColuna = anotacaoColuna.tamanho();
								tipoColuna = "VARCHAR("+tamanhoColuna+")";
							
						}else if (tipoParametro.equals(int.class)) {
							tipoColuna = "INT";

						}else if (tipoParametro.equals(EstadoCivil.class)) {
							tipoColuna = "VARCHAR(20)";
						}
						
						else {
							tipoColuna = "DESCONHECIDO";
						}

						if (i > 0) {
							sb.append(",");
						}

						sb.append("\n\t").append(nomeColuna).append(' ').append(tipoColuna);
					}
				
}
				{

					sb.append(",\n\tPRIMARY KEY( ");

					for (int i = 0, achou = 0; i < atributos.length; i++) {

						Field field = atributos[i];

						if (field.isAnnotationPresent(Coluna.class)) {

							Coluna anotacaoColuna = field.getAnnotation(Coluna.class);

							if (anotacaoColuna.pk()) {

								if (achou > 0) {
									sb.append(", ");
								}

								if (anotacaoColuna.nome().isEmpty()) {
									sb.append(field.getName().toUpperCase());
								} else {
									sb.append(anotacaoColuna.nome());
								}

								achou++;
							}

						}
					}

					sb.append(" )");
				}

				sb.append("\n);");

				return sb.toString();

			} catch (SecurityException e) {
				throw new RuntimeException(e);
			}
		
		}

		@Override
		protected String getDropTable(Class<Cliente> cl) {
			try {

				StringBuilder sb = new StringBuilder();
				
					String nomeTabela;
					if (cl.isAnnotationPresent(Tabela.class)) {

						Tabela anotacaoTabela = cl.getAnnotation(Tabela.class);
						nomeTabela = anotacaoTabela.value();

					} else {
						nomeTabela = cl.getSimpleName().toUpperCase();

					}
					sb.append("DROP TABLE ").append(nomeTabela).append(";");
				
				return sb.toString();

		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
			
		}

		@Override
		protected PreparedStatement getSqlInsert(Connection con, Object obj) {

			Class<? extends Object> cl = obj.getClass();

			StringBuilder sb = new StringBuilder();

			{
				String nomeTabela;
				if (cl.isAnnotationPresent(Tabela.class)) {

					Tabela anotacaoTabela = cl.getAnnotation(Tabela.class);
					nomeTabela = anotacaoTabela.value();

				} else {
					nomeTabela = cl.getSimpleName().toUpperCase();

				}
				sb.append("INSERT INTO ").append(nomeTabela).append(" (");
			}

			Field[] atributos = cl.getDeclaredFields();

			{
				for (int i = 0; i < atributos.length; i++) {

					Field field = atributos[i];

					String nomeColuna;

					if (field.isAnnotationPresent(Coluna.class)) {
						Coluna anotacaoColuna = field.getAnnotation(Coluna.class);

						if (anotacaoColuna.nome().isEmpty()) {
							nomeColuna = field.getName().toUpperCase();
						} else {
							nomeColuna = anotacaoColuna.nome();
						}

					} else {
						nomeColuna = field.getName().toUpperCase();
					}

					if (i > 0) {
						sb.append(", ");
					}

					sb.append(nomeColuna);
				}
			}

			sb.append(") VALUES (");

			for (int i = 0; i < atributos.length; i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append('?');
			}
			sb.append(')');

			String strSql = sb.toString();
			System.out.println(strSql);

			PreparedStatement ps = null;
			try {
				ps = con.prepareStatement(strSql);

				for (int i = 0; i < atributos.length; i++) {
					Field field = atributos[i];

					field.setAccessible(true);

					if (field.getType().equals(int.class)) {
						ps.setInt(i + 1, field.getInt(obj));

					} else if (field.getType().equals(String.class)) {
						ps.setString(i + 1, String.valueOf(field.get(obj)));

					} else if (field.getType().equals(EstadoCivil.class)) {
						ps.setString(i + 1, String.valueOf(field.get(obj)));
					} else {
						throw new RuntimeException("Tipo não suportado, falta implementar.");

					}
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			return ps;
		}


		@Override
		protected PreparedStatement getSqlSelectAll(Connection con, Object obj) {
			Class<? extends Object> cl = obj.getClass();

			StringBuilder sb = new StringBuilder();
			
			{
				String nomeTabela;
				if (cl.isAnnotationPresent(Tabela.class)) {

					Tabela anotacaoTabela = cl.getAnnotation(Tabela.class);
					nomeTabela = anotacaoTabela.value();

				} else {
					nomeTabela = cl.getSimpleName().toUpperCase();

				}
				sb.append("SELECT * FROM ").append(nomeTabela).append(";");
			}

			String strSql = sb.toString();
			System.out.println(strSql);

			PreparedStatement ps = null;
			try {
				ps = con.prepareStatement(strSql);
				
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}

			return ps;

		}

		@Override
			protected PreparedStatement getSqlSelectById(Connection con, Object obj) {

				Class<? extends Object> cl = obj.getClass();

				StringBuilder sb = new StringBuilder();

				{
					String nomeTabela;
					if (cl.isAnnotationPresent(Tabela.class)) {

						Tabela anotacaoTabela = cl.getAnnotation(Tabela.class);
						nomeTabela = anotacaoTabela.value();

					} else {
						nomeTabela = cl.getSimpleName().toUpperCase();

					}
					
					sb.append("SELECT ").append(nomeTabela).append(" ");
				}
				Field[] atributos = cl.getDeclaredFields();

				for (int i = 0, achou = 0; i < atributos.length; i++) {

					Field field = atributos[i];

					if (field.isAnnotationPresent(Coluna.class)) {

						Coluna anotacaoColuna = field.getAnnotation(Coluna.class);

						if (anotacaoColuna.pk()) {

							if (achou > 0) {
								sb.append(", ");
							}

							if (anotacaoColuna.nome().isEmpty()) {
								sb.append(field.getName().toUpperCase());
							} else {
								sb.append(anotacaoColuna.nome());
							}

							achou++;
						}

					}
				}
				
				
				String strSql = sb.toString();
				System.out.println(strSql);

				PreparedStatement ps = null;
				try {
					ps = con.prepareStatement(strSql);
					
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}

				return ps;	}

		@Override
		protected PreparedStatement getSqlUpdateById(Connection con, Object obj) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected PreparedStatement getSqlDeleteById(Connection con, Object obj) {
			// TODO Auto-generated method stub
			return null;
		}

		public static void main(String[] args) {
			new Montando();
		}
	}
