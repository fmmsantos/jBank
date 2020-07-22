/*
 * Esse script limpa a base de dados.
 * Deve ser executado depois de cada cenário de teste.
 */

truncate schema public restart identity and commit;