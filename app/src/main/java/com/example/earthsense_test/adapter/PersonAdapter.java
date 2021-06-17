package com.example.earthsense_test.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.earthsense_test.databinding.ListPersonsBinding;
import com.example.earthsense_test.model.Persons;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.DataViewHolder> {

    private List<Persons> personsList;
    private OnItemClick onItemClick;

    public PersonAdapter(List<Persons> personsList, OnItemClick onItemClick) {
        this.personsList = personsList;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DataViewHolder(ListPersonsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false), onItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        holder.bind(personsList.get(position));
    }

    @Override
    public int getItemCount() {
        return personsList.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {

        ListPersonsBinding binding;
        OnItemClick onItemClick;
        public DataViewHolder(@NonNull ListPersonsBinding binding, OnItemClick onItemClick) {
            super(binding.getRoot());
            this.binding = binding;
            this.onItemClick = onItemClick;
        }

        public void bind(Persons person){
            binding.tvPerson.setText(person.getName());
            binding.getRoot().setOnClickListener((view) -> {
                  onItemClick.onPersonClick(person);
               }
            );
        }
    }

    public interface OnItemClick{
         void onPersonClick(Persons persons);
    }
}
