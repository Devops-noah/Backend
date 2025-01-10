require 'csv'

# Définir les noms des membres de l'équipe
team_members = ["Alice", "Bob", "Charlie", "David"]

# Tableau 1: Tâches du projet
tasks = ["Analyse", "Conception", "Développement", "Tests", "Documentation"]

# Créer une matrice (2D) pour les tâches de chaque membre
work_table = Array.new(team_members.length) { Array.new(tasks.length, "Non fait") }

# Tableau 2: Nombre de lignes de code écrites par chaque membre
lines_of_code = {
  "Alice" => 150,
  "Bob" => 200,
  "Charlie" => 120,
  "David" => 180
}

# Sauvegarder dans un fichier CSV
def save_to_csv(work_table, lines_of_code, team_members, tasks)
  CSV.open("work_table.csv", "wb") do |csv|
    # En-têtes pour le tableau de travail
    csv << ["Membre"] + tasks
    team_members.each_with_index do |member, index|
      csv << [member] + work_table[index]
    end
  end

  CSV.open("lines_of_code.csv", "wb") do |csv|
    # En-têtes pour les lignes de code
    csv << ["Membre", "Lignes de code"]
    lines_of_code.each do |member, lines|
      csv << [member, lines]
    end
  end
end

# Charger les données depuis un fichier CSV
def load_from_csv
  work_table = []
  lines_of_code = {}

  if File.exist?("work_table.csv")
    CSV.foreach("work_table.csv", headers: true) do |row|
      member = row[0]
      work_table << row[1..-1]
    end
  end

  if File.exist?("lines_of_code.csv")
    CSV.foreach("lines_of_code.csv", headers: true) do |row|
      member = row[0]
      lines_of_code[member] = row[1].to_i
    end
  end

  [work_table, lines_of_code]
end

# Afficher les tableaux
def display_tables(work_table, lines_of_code, team_members, tasks)
  puts "Tableau des tâches:"
  puts "Membre\t\t#{tasks.join("\t")}"
  team_members.each_with_index do |member, index|
    puts "#{member}\t#{work_table[index].join("\t")}"
  end

  puts "\nTableau des lignes de code:"
  puts "Membre\tLignes de code"
  lines_of_code.each do |member, lines|
    puts "#{member}\t#{lines}"
  end
end


# Fonction pour mettre à jour une tâche
def update_task(work_table, member, task_index, status)
  member_index = work_table.index { |m| m[0] == member }
  if member_index
    work_table[member_index][task_index] = status
    puts "#{member} a maintenant : #{status} pour la tâche #{task_index + 1}"
  else
    puts "Membre non trouvé."
  end
end

# Fonction pour mettre à jour les lignes de code
def update_lines_of_code(lines_of_code, member, lines)
  lines_of_code[member] = lines
  puts "#{member} a maintenant #{lines} lignes de code."
end

# Charger les données existantes
work_table, lines_of_code = load_from_csv

# Afficher les tableaux avant modification
display_tables(work_table, lines_of_code, team_members, tasks)

# Exemple de mise à jour par l'utilisateur via la CLI
puts "\nMise à jour des tâches et des lignes de code :"

# Mettre à jour une tâche de Alice pour l'Analyse (index 0) en "Fait"
update_task(work_table, "Alice", 0, "Fait")

# Mettre à jour le nombre de lignes de code de Bob
update_lines_of_code(lines_of_code, "Bob", 250)

# Sauvegarder les modifications dans les fichiers CSV
save_to_csv(work_table, lines_of_code, team_members, tasks)

# Afficher les tableaux après modification
display_tables(work_table, lines_of_code, team_members, tasks)
