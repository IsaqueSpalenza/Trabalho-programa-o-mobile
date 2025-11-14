package ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atividade9.R;
import com.example.atividade9.Nome;
import java.util.List;

public class NomeAdapter extends RecyclerView.Adapter<NomeAdapter.NomeVH> {

    public interface OnDeleteClickListener {
        void onDeleteClick(Nome item);
    }

    public interface OnEditClickListener {
        void onEditClick(Nome item);
    }

    private List<Nome> dados;
    private final OnDeleteClickListener onDelete;
    private final OnEditClickListener onEdit;

    public NomeAdapter(List<Nome> dados,
                       OnDeleteClickListener onDelete,
                       OnEditClickListener onEdit) {
        this.dados = dados;
        this.onDelete = onDelete;
        this.onEdit = onEdit;
    }

    public void setDados(List<Nome> novos) {
        this.dados = novos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NomeVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nome, parent, false);
        return new NomeVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NomeVH h, int pos) {
        Nome item = dados.get(pos);
        h.tvNome.setText(item.getNome());

        h.btnEditar.setOnClickListener(v -> onEdit.onEditClick(item));
        h.btnExcluir.setOnClickListener(v -> onDelete.onDeleteClick(item));
    }

    @Override
    public int getItemCount() { return dados == null ? 0 : dados.size(); }

    static class NomeVH extends RecyclerView.ViewHolder {
        TextView tvNome;
        Button btnEditar, btnExcluir;

        NomeVH(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNome);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }
    }
}
